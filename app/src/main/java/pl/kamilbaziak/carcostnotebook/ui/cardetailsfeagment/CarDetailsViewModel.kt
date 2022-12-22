package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Odometer

class CarDetailsViewModel(
    private val tankFillDao: TankFillDao,
    private val odometerDao: OdometerDao,
    private val maintenanceDao: MaintenanceDao,
    private val carId: Long
) : ViewModel() {

    private val _lastOdometer = odometerDao.getLastCarOdometer(carId)
    val lastOdometer: LiveData<Odometer?> = _lastOdometer

    private val _tankFillCount = tankFillDao.getTankFillData(carId)
    val tankFillCount = _tankFillCount

    private val _odometerCount = odometerDao.getAllOdometerForCar(carId)
    val odometerCount = _odometerCount

    private val _maintenanceCount = maintenanceDao.getMaintenanceData(carId)
    val maintenanceCount = _maintenanceCount
}
