package pl.kamilbaziak.carcostnotebook.ui.cardetails.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Car

class DetailsViewModel(
    private val carDao: CarDao,
    private val odometerDao: OdometerDao,
    private val tankFillDao: TankFillDao,
    private val maintenanceDao: MaintenanceDao,
    carId: Long
): ViewModel() {

    private val detailsViewChannel = Channel<DetailsViewEvent>()
    val detailsViewEvent = detailsViewChannel.receiveAsFlow()

    val currentCarData = carDao.getCarById(carId)

    private val _allOdometerData = odometerDao.getOdometerLiveData(carId)
    val allOdometerData = _allOdometerData

    private val _allMaintenanceData = maintenanceDao.getMaintenanceLiveData(carId)
    val allMaintenance = _allMaintenanceData

    private val _allTankFillData = tankFillDao.getTankFillLiveData(carId)

    val allTankFillData = _allTankFillData

    fun onCarEdit() = viewModelScope.launch {
        detailsViewChannel.send(DetailsViewEvent.EditCar(currentCarData.value))
    }

    fun onCarDelete() = viewModelScope.launch {
        detailsViewChannel.send(DetailsViewEvent.ShowCarDeleteDialogMessage(currentCarData.value))
    }

    fun deleteCar() = viewModelScope.launch {
        currentCarData.value?.let {
            carDao.deleteCar(it)
            odometerDao.deleteOdometer(it.id)
            maintenanceDao.deleteMaintenance(it.id)
            tankFillDao.deleteTankFill(it.id)
            detailsViewChannel.send(DetailsViewEvent.CarDeleted)
        } ?: run { detailsViewChannel.send(DetailsViewEvent.ErrorDuringDeleteProcedure) }
    }

    sealed class DetailsViewEvent {

        object CarDeleted: DetailsViewEvent()
        object ErrorDuringDeleteProcedure: DetailsViewEvent()
        data class EditCar(val car: Car?) : DetailsViewEvent()
        data class ShowCarDeleteDialogMessage(val car: Car?) : DetailsViewEvent()
    }
}