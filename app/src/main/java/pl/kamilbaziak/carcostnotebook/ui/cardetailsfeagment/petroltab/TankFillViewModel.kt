package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill

class TankFillViewModel(
    private val tankFillDao: TankFillDao,
    private val carId: Long
) : ViewModel() {

    private val _tankFillAll = tankFillDao.getTankFillData(carId)
    val tankFillAll: LiveData<List<TankFill>> = _tankFillAll
}
