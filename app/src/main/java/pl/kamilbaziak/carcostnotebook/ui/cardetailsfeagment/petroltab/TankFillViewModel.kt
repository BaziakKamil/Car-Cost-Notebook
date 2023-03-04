package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill

class TankFillViewModel(
    private val tankFillDao: TankFillDao,
    private val odometerDao: OdometerDao,
    carId: Long
) : ViewModel() {

    private val tankFillChannel = Channel<TankFillEvent>()
    val tankFillEvent = tankFillChannel.receiveAsFlow()

    private val _tankFillAll = tankFillDao.getTankFillData(carId)
    val tankFillAll: LiveData<List<TankFill>> = _tankFillAll

    private val _tankFillMapped = MutableLiveData<List<Pair<TankFill, Odometer?>>>()
    val tankFillMapped: LiveData<List<Pair<TankFill, Odometer?>>> = _tankFillMapped

    private val deleteTankFill = MutableLiveData<TankFill>()

    fun onEditTankFill(tankFill: TankFill) = viewModelScope.launch {
        tankFillChannel.send(TankFillEvent.ShowTankFillEditDialogScreen(tankFill))
    }

    fun deleteTankFill() = viewModelScope.launch {
        deleteTankFill.value?.let { tankFill ->
            tankFillDao.deleteTankFill(tankFill)
            val pairedOdometer = odometerDao.getOdometerById(tankFill.odometerId)
            pairedOdometer?.let { odometerDao.deleteOdometer(it) }
            tankFillChannel.send(
                TankFillEvent.ShowUndoDeleteTankFillMessage(
                    tankFill,
                    pairedOdometer
                )
            )
        } ?: tankFillChannel.send(TankFillEvent.ShowDeleteErrorSnackbar)
    }

    fun onDeleteTankFill(tankFill: TankFill) = viewModelScope.launch {
        deleteTankFill.value = tankFill
        tankFillChannel.send(TankFillEvent.ShowTankFillDeleteDialogMessage)
    }

    fun onUndoDeleteTankFill(
        tankFill: TankFill,
        pairedOdometer: Odometer?
    ) = viewModelScope.launch {
        tankFillDao.addTankFill(tankFill)
        pairedOdometer?.let { odometerDao.addOdometer(pairedOdometer) }
    }

    fun setupTankFillData(list: List<TankFill>) = viewModelScope.launch {
        _tankFillMapped.value = list.map { tankFill ->
            val odometer = odometerDao.getOdometerById(tankFill.odometerId)
            Pair(tankFill, odometer)
        }
    }

    sealed class TankFillEvent {
        data class ShowTankFillEditDialogScreen(val tankFill: TankFill) : TankFillEvent()
        object ShowTankFillDeleteDialogMessage : TankFillEvent()
        object ShowDeleteErrorSnackbar : TankFillEvent()
        data class ShowUndoDeleteTankFillMessage(
            val tankFill: TankFill,
            val pairedOdometer: Odometer?
        ) : TankFillEvent()
        object ShowTankFillSavedConfirmationMessage : TankFillEvent()
    }
}
