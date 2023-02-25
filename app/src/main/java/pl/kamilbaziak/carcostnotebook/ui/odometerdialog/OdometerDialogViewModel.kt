package pl.kamilbaziak.carcostnotebook.ui.odometerdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer
import java.util.Date

class OdometerDialogViewModel(
    private val odometerDao: OdometerDao,
    private val carDao: CarDao,
    private val carId: Long
) : ViewModel() {

    private val _pickedDate = MutableLiveData(Date().time)
    val pickedDate: LiveData<Long> = _pickedDate

    fun changePickedDate(long: Long) {
        _pickedDate.value = long
    }

    fun updateOdometer(
        newOdometerValue: Double,
        odometer: Odometer
    ) = viewModelScope.launch {
        odometerDao.updateOdometer(
            odometer.copy(
                input = newOdometerValue,
                created = _pickedDate.value ?: Date().time
            )
        )
    }

    fun addOdometer(odometer: Double) = viewModelScope.launch {
        odometerDao.addOdometer(
            Odometer(
                0,
                carId,
                odometer,
                carDao.getCarById(carId)?.unit ?: UnitEnum.Kilometers,
                _pickedDate.value ?: Date().time,
                canBeDeleted = true
            )
        )
    }
}
