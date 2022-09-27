package pl.kamilbaziak.carcostnotebook.ui.mainviewfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.model.Car

class MainViewViewModel(
    private val carDao: CarDao
) : ViewModel() {

    private val _cars = carDao.getAllCars()
    val cars: LiveData<List<Car>> = _cars

    private val mainViewChannel = Channel<MainViewEvent>()
    val mainViewEvent = mainViewChannel.receiveAsFlow()

    fun addCar(car: Car) = viewModelScope.launch {
        carDao.addCar(car)
    }

    fun updateCar(car: Car) = viewModelScope.launch {
        carDao.updateCar(car)
    }

    fun deleteCar(car: Car) = viewModelScope.launch {
        carDao.deleteCar(car)
    }

    fun onAddNewCarClick() = viewModelScope.launch {
        mainViewChannel.send(MainViewEvent.AddnewCar)
    }

    fun onCarClick(car: Car) = viewModelScope.launch {
        mainViewChannel.send(MainViewEvent.NavigateToCarDetails(car))
    }

    sealed class MainViewEvent {

        data class NavigateToCarDetails(val car: Car): MainViewEvent()
        object AddnewCar: MainViewEvent()
    }
}