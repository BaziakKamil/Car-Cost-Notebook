package pl.kamilbaziak.carcostnotebook.ui.mainviewfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer

class MainViewViewModel(
    private val carDao: CarDao,
    private val odometerDao: OdometerDao
) : ViewModel() {

    private val _cars = carDao.getAllCars()
    val cars: LiveData<List<Car>> = _cars

    private val mainViewChannel = Channel<MainViewEvent>()
    val mainViewEvent = mainViewChannel.receiveAsFlow()

    fun deleteCar(car: Car) = viewModelScope.launch {
        carDao.deleteCar(car)
    }

    fun onAddNewCarClick() = viewModelScope.launch {
        mainViewChannel.send(MainViewEvent.AddnewCar)
    }

    fun onCarClick(car: Car) = viewModelScope.launch {
        mainViewChannel.send(MainViewEvent.NavigateToCarDetails(car, odometerDao.getLastCarOdometer(car.id)))
    }

    sealed class MainViewEvent {

        data class NavigateToCarDetails(
            val car: Car,
            val odometer: Odometer?
        ) : MainViewEvent()

        object AddnewCar : MainViewEvent()
    }
}
