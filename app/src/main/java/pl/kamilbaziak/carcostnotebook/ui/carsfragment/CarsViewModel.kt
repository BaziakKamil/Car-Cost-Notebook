package pl.kamilbaziak.carcostnotebook.ui.carsfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill

class CarsViewModel(
    private val carDao: CarDao,
    private val odometerDao: OdometerDao,
    private val maintenanceDao: MaintenanceDao,
    private val tankFillDao: TankFillDao
) : ViewModel() {

    private val _cars = carDao.getAllCars()
    val cars: LiveData<List<Car>> = _cars

    private val _carsMapped = MutableLiveData<List<Pair<Car, Odometer?>>>()
    val carsMapped: LiveData<List<Pair<Car, Odometer?>>> = _carsMapped

    private val deleteCar = MutableLiveData<Car>()

    private val mainViewChannel = Channel<MainViewEvent>()
    val mainViewEvent = mainViewChannel.receiveAsFlow()

    private var deletedTankFill = listOf<TankFill>()
    private var deletedMaintenance = listOf<Maintenance>()
    private var deletedOdometers = listOf<Odometer>()

    fun deleteCar() = viewModelScope.launch {
        saveDeletedCarData()
        deleteCar.value?.let {
            carDao.deleteCar(it)
            odometerDao.deleteOdometer(it.id)
            maintenanceDao.deleteMaintenance(it.id)
            tankFillDao.deleteTankFill(it.id)
            mainViewChannel.send(MainViewEvent.ShowUndoDeleteCarMessage)
        } ?: mainViewChannel.send(MainViewEvent.ShowDeleteErrorMessage)
    }

    private suspend fun saveDeletedCarData() {
        deleteCar.value?.let {
            deletedTankFill = tankFillDao.getTankFillDataForCar(it.id)
            deletedMaintenance = maintenanceDao.getMaintenanceDataForCar(it.id)
            deletedOdometers = odometerDao.getAllOdometerDataForCar(it.id)
        }
    }

    fun onUndoDeleteCar() = viewModelScope.launch {
        deleteCar.value?.let { carDao.addCar(it) }
        deletedTankFill.onEach { tankFillDao.addTankFill(it) }
        deletedMaintenance.onEach { maintenanceDao.addMaintenance(it) }
        deletedOdometers.onEach { odometerDao.addOdometer(it) }
    }

    fun onAddNewCarClick() = viewModelScope.launch {
        mainViewChannel.send(MainViewEvent.AddNewCar)
    }

    fun onCarClick(car: Car) = viewModelScope.launch {
        mainViewChannel.send(
            MainViewEvent.NavigateToCarDetails(
                car,
                odometerDao.getLastCarOdometer(car.id)
            )
        )
    }

    fun onCarEdit(car: Car) = viewModelScope.launch {
        mainViewChannel.send(MainViewEvent.ShowCarEditDialogScreen(car))
    }

    fun onCarDelete(car: Car) = viewModelScope.launch {
        deleteCar.value = car
        mainViewChannel.send(MainViewEvent.ShowCarDeleteDialogMessage(car))
    }

    fun setupCarMappedData(list: List<Car>) = viewModelScope.launch {
        _carsMapped.value = list.map { car ->
            Pair(car, odometerDao.getLastCarOdometer(car.id))
        }
    }

    sealed class MainViewEvent {

        data class NavigateToCarDetails(
            val car: Car,
            val odometer: Odometer?
        ) : MainViewEvent()

        object AddNewCar : MainViewEvent()
        data class ShowCarEditDialogScreen(val car: Car) : MainViewEvent()
        data class ShowCarDeleteDialogMessage(val car: Car) : MainViewEvent()
        object ShowUndoDeleteCarMessage : MainViewEvent()
        object ShowDeleteErrorMessage : MainViewEvent()
    }
}
