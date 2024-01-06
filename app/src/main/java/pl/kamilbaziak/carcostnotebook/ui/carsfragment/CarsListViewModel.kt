package pl.kamilbaziak.carcostnotebook.ui.carsfragment

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.Constants.BACKUP_DIRECTORY
import pl.kamilbaziak.carcostnotebook.Constants.BACKUP_EXTENSION
import pl.kamilbaziak.carcostnotebook.Constants.BACKUP_NAME
import pl.kamilbaziak.carcostnotebook.Constants.BACKUP_SEPARATOR
import pl.kamilbaziak.carcostnotebook.DateUtils
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.Calendar

class CarsListViewModel(
    private val application: Application,
    private val carDao: CarDao,
    private val odometerDao: OdometerDao,
    private val maintenanceDao: MaintenanceDao,
    private val tankFillDao: TankFillDao
) : AndroidViewModel(application) {

    private val context: Context
        get() = application.applicationContext

    private val TAG = "CarsViewModel"
    private val gson = Gson()

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

    fun exportDatabase() = viewModelScope.launch {
        val carList = carDao.getAllCarList()
        val tankFillList = tankFillDao.getAllTankFill()
        val maintenanceList = maintenanceDao.getAllMaintenance()
        val odometerList = odometerDao.getAllOdometer()

        val carJSON = gson.toJson(carList)
        val tankJSON = gson.toJson(tankFillList)
        val maintenanceJSON = gson.toJson(maintenanceList)
        val odometerJSON = gson.toJson(odometerList)

        saveToStorage(arrayOf(carJSON, tankJSON, maintenanceJSON, odometerJSON))
    }

    fun importDatabase() = viewModelScope.launch {
        mainViewChannel.send(MainViewEvent.OpenFilePicker)
    }

    private fun saveToStorage(jsonArray: Array<String>) = viewModelScope.launch {
        val backupDirectory = getAppDirectoryInDocuments()
        var message = context.getString(R.string.car_database_exported_successfully)
        try {
            val fileName = "$BACKUP_NAME${DateUtils.formatBackupDateFromLong(Calendar.getInstance().timeInMillis)}$BACKUP_EXTENSION"
            val backupFile = jsonArray.joinToString(BACKUP_SEPARATOR)
            File(backupDirectory, fileName).writeBytes(backupFile.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            message = context.getString(R.string.error_occurred_during_saving_backup)
        }
        mainViewChannel.send(MainViewEvent.ShowSnackbarMessage(message))
    }

    private fun getAppDirectoryInDocuments(): File {
        val downloadsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val appDirectory = File(downloadsDirectory, BACKUP_DIRECTORY)

        if (!appDirectory.exists()) {
            appDirectory.mkdir()
        }

        return appDirectory
    }

    fun prepareFileForImportToDatabase(file: String) = viewModelScope.launch {
        val backupList = file.split(BACKUP_SEPARATOR)
        if (backupList.isEmpty() || backupList.size < 4) {
            mainViewChannel.send(MainViewEvent.ShowSnackbarMessage(context.getString(R.string.error_occurred_during_import_wrong_file)))
            return@launch
        }
        putToDatabaseFromJson(backupList)
    }

    private fun putToDatabaseFromJson(backupList: List<String>) = viewModelScope.launch {
        try {
                val cars = backupList[0]
                val tankFills = backupList[1]
                val maintenances = backupList[2]
                val odometers = backupList[3]

                val carsData = gson.fromJson<List<Car>>(
                    cars,
                    object : TypeToken<List<Car>>() {}.type
                )
                val tankFillData = gson.fromJson<List<TankFill>>(
                    tankFills,
                    object : TypeToken<List<TankFill>>() {}.type
                )
                val maintenanceData = gson.fromJson<List<Maintenance>>(
                    maintenances,
                    object : TypeToken<List<Maintenance>>() {}.type
                )
                val odometerData = gson.fromJson<List<Odometer>>(
                    odometers,
                    object : TypeToken<List<Odometer>>() {}.type
                )

                if (carsData.isNotEmpty()) {
                    carsData.forEach { carDao.addCar(it) }
                }
                if (tankFillData.isNotEmpty()) {
                    tankFillData.forEach { tankFillDao.addTankFill(it) }
                }
                if (maintenanceData.isNotEmpty()) {
                    maintenanceData.forEach { maintenanceDao.addMaintenance(it) }
                }
                if (odometerData.isNotEmpty()) {
                    odometerData.forEach { odometerDao.addOdometer(it) }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    sealed class MainViewEvent {

        data class NavigateToCarDetails(
            val car: Car,
            val odometer: Odometer?
        ) : MainViewEvent()

        object AddNewCar : MainViewEvent()
        object ShowUndoDeleteCarMessage : MainViewEvent()
        object ShowDeleteErrorMessage : MainViewEvent()
        object OpenFilePicker : MainViewEvent()
        data class ShowSnackbarMessage(val message: String) : MainViewEvent()
        data class ShowCarEditDialogScreen(val car: Car) : MainViewEvent()
        data class ShowCarDeleteDialogMessage(val car: Car) : MainViewEvent()
        data class ShowErrorDialogMessage(val message: String) : MainViewEvent()
    }
}