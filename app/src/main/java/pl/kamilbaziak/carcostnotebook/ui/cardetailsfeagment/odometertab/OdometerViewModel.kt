package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val deleteOdometer = MutableLiveData<Odometer>()

    fun onEditOdometer(odometer: Odometer) = viewModelScope.launch {
        odometerChannel.send(OdometerEvent.ShowOdometerEditDialogScreen(odometer))
    }

    fun deleteOdometer() = viewModelScope.launch {
        deleteOdometer.value?.let {
            odometerDao.deleteOdometer(it)
            odometerChannel.send(OdometerEvent.ShowUndoDeleteOdometerMessage(it))
        } ?: odometerChannel.send(OdometerEvent.ShowDeleteErrorSnackbar)
    }

    fun onDeleteOdometer(odometer: Odometer) = viewModelScope.launch {
        deleteOdometer.value = odometer
        odometerChannel.send(OdometerEvent.ShowOdometerDeleteDialogMessage)
    }

    fun onUndoDeleteOdometer(odometer: Odometer) = viewModelScope.launch {
        odometerDao.addOdometer(odometer)
    }

    sealed class OdometerEvent {
        data class ShowOdometerEditDialogScreen(val odometer: Odometer) : OdometerEvent()
        object ShowOdometerDeleteDialogMessage : OdometerEvent()
        object ShowDeleteErrorSnackbar : OdometerEvent()
        data class ShowUndoDeleteOdometerMessage(val odometer: Odometer) : OdometerEvent()
        object ShowOdometerSavedConfirmationMessage : OdometerEvent()
    }
}
