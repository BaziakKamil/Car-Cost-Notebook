package pl.kamilbaziak.carcostnotebook.ui.maintenancedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import java.util.Date

class MaintenanceDialogViewModel(
    private val carDao: CarDao,
    private val maintenanceDao: MaintenanceDao,
    private val odometerDao: OdometerDao
) : ViewModel() {

    private val _pickedDate = MutableLiveData(Date().time)
    val pickedDate: LiveData<Long> = _pickedDate

    private val _pickedDueDate = MutableLiveData<Long>()
    val pickedDueDate: LiveData<Long> = _pickedDueDate

    fun changePickedDate(long: Long) {
        _pickedDate.value = long
    }

    fun changePickedDueDate(long: Long) {
        _pickedDueDate.value = long
    }

    fun addMaintenance(
        carId: Long,
        name: String,
        price: Double?,
        odometer: Double?,
        description: String?
    ) =
        viewModelScope.launch {
            maintenanceDao.addMaintenance(
                Maintenance(
                    0,
                    carId,
                    name,
                    price,
                    odometer?.let {
                        odometerDao.addOdometer(
                            Odometer(
                                0,
                                carId,
                                odometer,
                                carDao.getCarById(carId)?.unit ?: UnitEnum.Kilometers,
                                pickedDate.value ?: Date().time
                            )
                        )
                    },
                    pickedDate.value ?: Date().time,
                    pickedDueDate.value,
                    false,
                    description
                )
            )
        }
}
