package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.details

import androidx.lifecycle.ViewModel
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao

class DetailsViewModel(
    private val carDao: CarDao,
    private val odometerDao: OdometerDao,
    private val tankFillDao: TankFillDao,
    private val maintenanceDao: MaintenanceDao,
    carId: Long
): ViewModel() {

    val currentCarData = carDao.getCarById(carId)

    private val _allOdometerData = odometerDao.getAllOdometerForCar(carId)
    val allOdometerData = _allOdometerData

    private val _allMaintenanceData = maintenanceDao.getMaintenanceData(carId)
    val allMaintenance = _allMaintenanceData

    private val _allTankFillData = tankFillDao.getTankFillData(carId)
    val allTankFillData = _allTankFillData
}