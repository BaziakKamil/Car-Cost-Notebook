package pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import java.util.*

class AddNewCarViewModel(
    private val carDao: CarDao,
    private val odometerDao: OdometerDao
) : ViewModel() {

    private val _addNewCarChannel = Channel<AddNewCarEvent>()
    val addNewCarEvent = _addNewCarChannel.receiveAsFlow()

    fun addCar(
        brand: String,
        model: String,
        carYear: Int,
        licensePlate: String,
        engineEnum: EngineEnum,
        odometer: Double,
        unitEnum: UnitEnum,
        description: String
    ) = viewModelScope.launch {
        odometerDao.addOdometer(
            Odometer(
                0,
                carDao.addCar(
                    Car(
                        0,
                        brand,
                        model,
                        carYear,
                        licensePlate,
                        engineEnum,
                        unitEnum,
                        description
                    )
                ),
                odometer,
                System.currentTimeMillis()
            )
        )
        _addNewCarChannel.send(AddNewCarEvent.NavigateBack)
    }

    fun updateCar(car: Car) = viewModelScope.launch {
        carDao.updateCar(car)
        _addNewCarChannel.send(AddNewCarEvent.NavigateBack)
    }

    sealed class AddNewCarEvent {

        object NavigateBack : AddNewCarEvent()
    }
}
