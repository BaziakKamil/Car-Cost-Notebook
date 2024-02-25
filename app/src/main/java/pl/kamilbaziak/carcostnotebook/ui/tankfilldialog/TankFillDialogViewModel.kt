package pl.kamilbaziak.carcostnotebook.ui.tankfilldialog

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import pl.kamilbaziak.carcostnotebook.ui.DialogEvents
import java.util.Date

class TankFillDialogViewModel(
    private val application: Application,
    private val carDao: CarDao,
    private val tankFillDao: TankFillDao,
    private val odometerDao: OdometerDao,
    private val carId: Long
) : AndroidViewModel(application) {

    val context: Context
        get() = application.applicationContext

    private val _pickedDate = MutableLiveData(Date().time)
    val pickedDate: LiveData<Long> = _pickedDate

    private val _odometerForTankFill = MutableLiveData<Odometer>()
    val odometerForTankFill: LiveData<Odometer> = _odometerForTankFill

    val currentCar: LiveData<Car?> = carDao.getCarById(carId)

    private val _tankFillEvents = MutableStateFlow<DialogEvents?>(null)
    val tankFillEvents = _tankFillEvents.asStateFlow()

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
                        canBeDeleted = false,
                        description = context.getString(R.string.tank_fill, petrolStation)
                    )
                ),
                computerReading,
                petrolStation,
                pickedDate.value ?: Date().time
            )
        )
        _tankFillEvents.emit(DialogEvents.Dismiss)
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
                        canBeDeleted = false,
                        description = context.getString(R.string.tank_fill, petrolStation)
                    )
                ),
                computerReading = computerReading,
                petrolStation = petrolStation,
                created = pickedDate.value ?: Date().time
            )
        )
    }
}
