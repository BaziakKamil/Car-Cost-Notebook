package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.DataState

class MaintenanceViewModel(
    private val maintenanceDao: MaintenanceDao,
    private val odometerDao: OdometerDao,
    private val carId: Long
) : ViewModel() {

    private val maintenanceChannel = Channel<MaintenanceEvent>()
    val maintenanceEvent = maintenanceChannel.receiveAsFlow()

    private val deleteMaintenance = MutableLiveData<Maintenance>()

    private val _dataState = MutableLiveData<DataState>(DataState.Progress)
    val dataState: LiveData<DataState> = _dataState

    init { prepareMaintenanceData() }

    private fun prepareMaintenanceData()  {
        _dataState.value = DataState.Progress
        viewModelScope.launch {
            maintenanceDao.getMaintenanceData(carId).let { list ->
                _dataState.value = if (list.isNotEmpty()) {
                    DataState.Found(list.sortedByDescending { it.created })
                } else {
                    DataState.NotFound
                }
            }
        }
    }

    fun onEditMaintenance(maintenance: Maintenance) = viewModelScope.launch {
        maintenanceChannel.send(MaintenanceEvent.ShowCarEditDialogScreen(maintenance))
    }

    fun deleteMaintenance() = viewModelScope.launch {
        deleteMaintenance.value?.let { maintenance ->
            maintenanceDao.deleteMaintenance(maintenance)
            val pairedOdometer = maintenance.odometerId?.let { odometerDao.getOdometerById(maintenance.odometerId) }
            pairedOdometer?.let { odometerDao.deleteOdometer(it) }
            maintenanceChannel.send(
                MaintenanceEvent.ShowUndoDeleteMaintenanceMessage(
                    maintenance,
                    pairedOdometer
                )
            )
        } ?: maintenanceChannel.send(MaintenanceEvent.ShowDeleteErrorSnackbar)
    }

    fun onDeleteMaintenance(maintenance: Maintenance) = viewModelScope.launch {
        deleteMaintenance.value = maintenance
        maintenanceChannel.send(MaintenanceEvent.ShowMaintenanceDeleteDialogMessage(maintenance))
    }

    fun onUndoDeleteMaintenance(maintenance: Maintenance, pairedOdometer: Odometer?) =
        viewModelScope.launch {
            maintenanceDao.addMaintenance(maintenance)
            pairedOdometer?.let { odometerDao.addOdometer(it) }
        }

    sealed class MaintenanceEvent {
        data class ShowCarEditDialogScreen(val maintenance: Maintenance) : MaintenanceEvent()
        data class ShowMaintenanceDeleteDialogMessage(val maintenance: Maintenance) :
            MaintenanceEvent()

        object ShowDeleteErrorSnackbar : MaintenanceEvent()
        data class ShowUndoDeleteMaintenanceMessage(
            val maintenance: Maintenance,
            val pairedOdometer: Odometer?
        ) :
            MaintenanceEvent()

        object ShowMaintenanceSavedConfirmationMessage : MaintenanceEvent()
    }
}
