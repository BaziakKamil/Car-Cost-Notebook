package pl.kamilbaziak.carcostnotebook.ui.carsfragment

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.Constants.BACKUP_NAME
import pl.kamilbaziak.carcostnotebook.DateUtils
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class CarsListViewModel(
    private val application: Application,
    private val carDao: CarDao,
    private val odometerDao: OdometerDao,
    private val maintenanceDao: MaintenanceDao,
    private val tankFillDao: TankFillDao
) : AndroidViewModel(application) {

    private val context = application.applicationContext

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

        val finalJSON = gson.toJson(listOf(carJSON, tankJSON, maintenanceJSON, odometerJSON))

        saveToStorage(finalJSON)
    }

    fun importDatabase() = viewModelScope.launch {
        getBackupFilesDialog()
    }

    private fun saveToStorage(json: String) {
//        val path = context.filesDir.path
//        val directory = File(path, "LET")
//        directory.mkdirs()
//        val file = File(directory, "carNotebookBackup_${DateUtils.formatDateFromLong(Calendar.getInstance().timeInMillis)}.ccn")
//        FileOutputStream(file).use {
//            it.write(json.toByteArray())
//            it.close()
//        }
        val fileName = "$BACKUP_NAME${DateUtils.formatBackupDateFromLong(Calendar.getInstance().timeInMillis)}.ccn"
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
            it.close()
        }
    }

    private fun getBackupFilesDialog() {
        val files = context.filesDir.listFiles()
        val backupFiles = files?.filter {
            it.name.contains(BACKUP_NAME)
        }
        if (backupFiles.isNullOrEmpty()) {
            //todo return information that there is no backup files
            return
        }

        //todo show dialog with files listed
    }

    private fun readFromStorage() {

    }

//    private fun exportDatabase(context: Context, fileName: String): Boolean {
//        try {
//            val currentDB = File(context.getDatabasePath(localDbName).path)
//            val backupDB = File(Environment.getExternalStorageDirectory().path, backupDBName)
//            if (currentDB.exists()) {
//                val src = FileInputStream(currentDB).channel
//                val dst = FileOutputStream(backupDB).channel
//                dst.transferFrom(src, 0, src.size())
//                src.close()
//                dst.close()
//            } else {
//                Log.e(TAG, "SD can't write data!")
//                return false
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

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