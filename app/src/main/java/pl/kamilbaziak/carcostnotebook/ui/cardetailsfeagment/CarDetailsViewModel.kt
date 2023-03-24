package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import androidx.lifecycle.ViewModel
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao

class CarDetailsViewModel(
    tankFillDao: TankFillDao,
    odometerDao: OdometerDao,
    maintenanceDao: MaintenanceDao,
    carId: Long
) : ViewModel() {

    private val _tankFillCount = tankFillDao.getTankFillLiveData(carId)
    val tankFillCount = _tankFillCount

    private val _odometerCount = odometerDao.getOdometerLiveData(carId)
    val odometerCount = _odometerCount

    private val _maintenanceCount = maintenanceDao.getMaintenanceLiveData(carId)
    val maintenanceCount = _maintenanceCount
}
