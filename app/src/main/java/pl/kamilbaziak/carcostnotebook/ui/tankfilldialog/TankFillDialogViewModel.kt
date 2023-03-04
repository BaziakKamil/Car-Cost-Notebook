package pl.kamilbaziak.carcostnotebook.ui.tankfilldialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import java.util.Date

class TankFillDialogViewModel(
    private val carDao: CarDao,
    private val tankFillDao: TankFillDao,
    private val odometerDao: OdometerDao
) : ViewModel() {

    private val _pickedDate = MutableLiveData(Date().time)
    val pickedDate: LiveData<Long> = _pickedDate

    private val _odometerForTankFill = MutableLiveData<Odometer>()
    val odometerForTankFill: LiveData<Odometer> = _odometerForTankFill

    fun changePickedDate(long: Long) {
        _pickedDate.value = long
    }

    fun getOdometerForTankFill(odometerId: Long) = viewModelScope.launch {
        odometerDao.getOdometerById(odometerId)?.let {
            _odometerForTankFill.value = it
        }
    }

    fun addTankFill(
        carId: Long,
        petrolEnum: PetrolEnum,
        quantity: Double,
        petrolPrice: Double?,
        distanceFromLastFill: Double?,
        odometer: Double,
        computerReading: Double?,
        petrolStation: String,
        tankFill: TankFill?
    ) = viewModelScope.launch {
        tankFill?.let {
            editTankFill(
                petrolEnum,
                quantity,
                petrolPrice,
                distanceFromLastFill,
                odometer,
                computerReading,
                petrolStation,
                it
            )
        } ?: tankFillDao.addTankFill(
            TankFill(
                0,
                carId,
                petrolEnum,
                quantity,
                petrolPrice,
                distanceFromLastFill,
                odometerDao.addOdometer(
                    Odometer(
                        0,
                        carId,
                        odometer,
                        carDao.getCarById(carId).value?.unit ?: UnitEnum.Kilometers,
                        pickedDate.value ?: Date().time,
                        canBeDeleted = false
                    )
                ),
                computerReading,
                petrolStation,
                pickedDate.value ?: Date().time
            )
        )
    }

    private fun editTankFill(
        petrolEnum: PetrolEnum,
        quantity: Double,
        petrolPrice: Double?,
        distanceFromLastFill: Double?,
        odometer: Double,
        computerReading: Double?,
        petrolStation: String,
        tankFill: TankFill
    ) = viewModelScope.launch {
        odometerDao.deleteOdometerById(tankFill.odometerId)
        tankFillDao.updateTankFill(
            tankFill.copy(
                petrolEnum = petrolEnum,
                quantity = quantity,
                petrolPrice = petrolPrice,
                distanceFromLastTankFill = distanceFromLastFill,
                odometerId = odometerDao.addOdometer(
                    Odometer(
                        0,
                        tankFill.carId,
                        odometer,
                        carDao.getCarById(tankFill.carId).value?.unit ?: UnitEnum.Kilometers,
                        pickedDate.value ?: Date().time,
                        canBeDeleted = false
                    )
                ),
                computerReading = computerReading,
                petrolStation = petrolStation,
                created = pickedDate.value ?: Date().time
            )
        )
    }
}
