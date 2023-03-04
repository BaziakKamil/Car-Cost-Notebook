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

    private val _odometerForMaintenance = MutableLiveData<Odometer>()
    val odometerForMaintenance: LiveData<Odometer> = _odometerForMaintenance

    fun changePickedDate(long: Long) {
        _pickedDate.value = long
    }

    fun getOdometerForMaintenance(odometerId: Long) = viewModelScope.launch {
        odometerDao.getOdometerById(odometerId)?.let {
            _odometerForMaintenance.value = it
        }
    }

    fun changePickedDueDate(long: Long?) = long?.let { _pickedDueDate.value }

    fun addMaintenance(
        carId: Long,
        name: String,
        price: Double?,
        odometer: Double?,
        description: String?,
        maintenance: Maintenance?
    ) =
        viewModelScope.launch {
            maintenance?.let {
                editMaintenance(name, price, odometer, description, it)
            } ?: maintenanceDao.addMaintenance(
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
                                carDao.getCarById(carId).value?.unit ?: UnitEnum.Kilometers,
                                pickedDate.value ?: Date().time,
                                canBeDeleted = false
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

    private fun editMaintenance(
        name: String,
        price: Double?,
        odometer: Double?,
        description: String?,
        maintenance: Maintenance
    ) =
        viewModelScope.launch {
            var addedOdometerId: Long? = null

            maintenance.odometerId?.let {
                odometerDao.deleteOdometerById(it)
            }

            odometer?.let {
                addedOdometerId = odometerDao.addOdometer(
                    Odometer(
                        0,
                        maintenance.carId,
                        it,
                        carDao.getCarById(maintenance.carId).value?.unit ?: UnitEnum.Kilometers,
                        pickedDate.value ?: Date().time,
                        canBeDeleted = false
                    )
                )
            }

            maintenanceDao.updateMaintenance(
                maintenance.copy(
                    name = name,
                    price = price,
                    odometerId = addedOdometerId,
                    description = description,
                    created = pickedDate.value ?: Date().time,
                    dueDate = pickedDueDate.value,
                )
            )
        }
}
