package pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.model.Car

class AddNewCarViewModel(
    private val carDao: CarDao
): ViewModel() {

    fun addCar(car: Car) = viewModelScope.launch {
        carDao.addCar(car)
    }

    fun updateCar(car: Car) = viewModelScope.launch {
        carDao.updateCar(car)
    }
}