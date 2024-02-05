package pl.kamilbaziak.carcostnotebook.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer

class MainViewModel : ViewModel() {

    private val _mainViewModelEvents = MutableSharedFlow<MainActivityEvent>()
    val mainViewModelEvents = _mainViewModelEvents.asSharedFlow()

    fun openAddNewCar(title: String, car: Car? = null) = viewModelScope.launch {
        _mainViewModelEvents.emit(
            MainActivityEvent.OpenAddNewCar(
                title = title,
                car = car
            )
        )
    }

    fun openCarDetails(title: String, car: Car, odometer: Odometer?) = viewModelScope.launch {
        _mainViewModelEvents.emit(
            MainActivityEvent.OpenCarDetails(
                title = title,
                car = car,
                odometer = odometer
            )
        )
    }
}