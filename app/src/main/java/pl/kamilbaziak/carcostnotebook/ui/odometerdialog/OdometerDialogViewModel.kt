package pl.kamilbaziak.carcostnotebook.ui.odometerdialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.model.Odometer

class OdometerDialogViewModel(
    private val odometerDao: OdometerDao
) : ViewModel() {

    fun addOdometer(odometer: Odometer) = viewModelScope.launch {
        odometerDao.addOdometer(odometer)
    }
}