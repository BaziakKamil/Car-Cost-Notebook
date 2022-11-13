package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.model.Maintenance

class MaintenanceViewModel(
    private val maintenanceDao: MaintenanceDao,
    private val carId: Long
) : ViewModel() {

    private val maintenanceChannel = Channel<MaintenanceEvent>()
    val maintenanceEvent = maintenanceChannel.receiveAsFlow()

    private val _maintenanceAll = maintenanceDao.getMaintenanceData(carId)
    val maintenanceAll: LiveData<List<Maintenance>> = _maintenanceAll

    fun onDeleteMaintenance(maintenance: Maintenance) = viewModelScope.launch{
        maintenanceDao.deleteMaintenance(maintenance)
    }

    fun onUndoDeleteMaintenance(maintenance: Maintenance) = viewModelScope.launch {
        maintenanceDao.addMaintenance(maintenance)
    }

    sealed class MaintenanceEvent {
        data class ShowUndoDeleteMaintenanceMessage(val maintenance: Maintenance) : MaintenanceEvent()
        object ShowMaintenanceSavedConfirmationMessage : MaintenanceEvent()
    }
}
