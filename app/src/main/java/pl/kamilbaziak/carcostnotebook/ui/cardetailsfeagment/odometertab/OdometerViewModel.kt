package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.model.Odometer

class OdometerViewModel(
    private val odometerDao: OdometerDao,
    private val carId: Long
) : ViewModel() {

    private val odometerChannel = Channel<OdometerEvent>()
    val odometerEvent = odometerChannel.receiveAsFlow()

    private val _odometerAll = odometerDao.getAllOdometerForCar(carId)
    val odometerAll: LiveData<List<Odometer>> = _odometerAll

    fun onDeleteOdometer(odometer: Odometer) = viewModelScope.launch {
        odometerDao.deleteOdometer(odometer)
    }

    fun onUndoDeleteOdometer(odometer: Odometer) = viewModelScope.launch {
        odometerDao.addOdometer(odometer)
    }

    sealed class OdometerEvent {
        data class ShowUndoDeleteOdometerMessage(val odometer: Odometer) : OdometerEvent()
        object ShowOdometerSavedConfirmationMessage : OdometerEvent()
    }
}
