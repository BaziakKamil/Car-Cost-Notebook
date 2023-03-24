package pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import java.util.*

class AddNewCarViewModel(
    private val carDao: CarDao,
    private val odometerDao: OdometerDao
) : ViewModel() {

    private val _addNewCarChannel = Channel<AddNewCarEvent>()
    val addNewCarEvent = _addNewCarChannel.receiveAsFlow()

    private val _lastOdometer = MutableLiveData<Odometer?>()
    val lastOdometer: LiveData<Odometer?> = _lastOdometer

    private val _odometerAll = MutableLiveData<List<Odometer>>()
    val odometerAll: LiveData<List<Odometer>> = _odometerAll

    private val _pickedDate = MutableLiveData(Date().time)
    val pickedDate: LiveData<Long> = _pickedDate

    fun addCar(car: Car, odometer: Double) = viewModelScope.launch {
        odometerDao.addOdometer(
            Odometer(
                0,
                carDao.addCar(car),
                odometer,
                car.unit,
                System.currentTimeMillis(),
                canBeDeleted = false
            )
        )
        _addNewCarChannel.send(AddNewCarEvent.NavigateBack)
    }

    fun updateCar(car: Car, odometer: Odometer?, newOdometerValue: Double) = viewModelScope.launch {
        odometer?.let {
            odometerDao.updateOdometer(it.copy(input = newOdometerValue))
        } ?: run {
            odometerDao.addOdometer(
                Odometer(
                    0,
                    car.id,
                    newOdometerValue,
                    car.unit,
                    System.currentTimeMillis(),
                    canBeDeleted = false
                )
            )
        }
        carDao.updateCar(car)
        _addNewCarChannel.send(AddNewCarEvent.NavigateBack)
    }

    fun getAllOdometer(car: Car) = viewModelScope.launch {
        _odometerAll.value = odometerDao.getOdometerLiveData(car.id).value
    }

    fun getLastOdometer(car: Car) = viewModelScope.launch {
        _lastOdometer.value = odometerDao.getLastCarOdometer(car.id)
    }

    fun changePickedDate(long: Long) {
        _pickedDate.value = long
    }

    sealed class AddNewCarEvent {

        object NavigateBack : AddNewCarEvent()
    }
}
