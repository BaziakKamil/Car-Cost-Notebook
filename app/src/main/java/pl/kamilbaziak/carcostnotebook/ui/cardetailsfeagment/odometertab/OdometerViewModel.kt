package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.model.Odometer

class OdometerViewModel(
    private val odometerDao: OdometerDao,
    private val carId: Long
) : ViewModel() {

    private val _odometerAll = odometerDao.getAllOdometerForCar(carId)
    val odometerAll: LiveData<List<Odometer>> = _odometerAll
}
