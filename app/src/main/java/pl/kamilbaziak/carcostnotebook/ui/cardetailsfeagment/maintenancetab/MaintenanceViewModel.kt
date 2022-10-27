package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.model.Maintenance

class MaintenanceViewModel(
    private val maintenanceDao: MaintenanceDao,
    private val carId: Long
) : ViewModel() {

    private val _maintenanceAll = maintenanceDao.getMaintenanceData(carId)
    val maintenanceAll: LiveData<List<Maintenance>> = _maintenanceAll
}
