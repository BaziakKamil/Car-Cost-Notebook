package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.TankFill

class TankFillViewModel(
    private val tankFillDao: TankFillDao,
    private val carId: Long
) : ViewModel() {

    private val tankFillChannel = Channel<TankFilEvent>()
    val tankFillEvent = tankFillChannel.receiveAsFlow()

    private val _tankFillAll = tankFillDao.getTankFillData(carId)
    val tankFillAll: LiveData<List<TankFill>> = _tankFillAll

    fun onDeleteTankFill(tankFill: TankFill) = viewModelScope.launch {
        tankFillDao.deleteTankFill(tankFill)
    }

    fun onUndoDeleteTankFill(tankFill: TankFill) = viewModelScope.launch {
        tankFillDao.addTankFill(tankFill)
    }

    sealed class TankFilEvent {
        data class ShowUndoDeleteTankFillMessage(val tankFill: TankFill) : TankFilEvent()
        object ShowTankFillSavedConfirmationMessage : TankFilEvent()
    }
}
