package pl.kamilbaziak.carcostnotebook.ui.tankfilldialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import java.util.Date

class TankFillDialogViewModel(
    private val tankFillDao: TankFillDao,
    private val odometerDao: OdometerDao
) : ViewModel() {

    private val _pickedDate = MutableLiveData(Date().time)
    val pickedDate: LiveData<Long> = _pickedDate

    fun changePickedDate(long: Long) {
        _pickedDate.value = long
    }

    fun addTankFill(
        carId: Long,
        petrolEnum: PetrolEnum,
        quantity: Int,
        petrolPrice: Double?,
        odometer: Double?,
        computerReading: Double?,
        petrolStation: String?
    ) = viewModelScope.launch {
        tankFillDao.addTankFill(
            TankFill(
                0,
                carId,
                petrolEnum,
                quantity,
                petrolPrice,
                odometer?.let {
                    odometerDao.addOdometer(
                        Odometer(0, carId, it, pickedDate.value ?: Date().time)
                    )
                },
                computerReading,
                petrolStation,
                pickedDate.value!!
            )
        )
    }
}
