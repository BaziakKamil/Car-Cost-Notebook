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
    private val carId: Long
) : ViewModel() {

    private val tankFillChannel = Channel<TankFilEvent>()
    val tankFillEvent = tankFillChannel.receiveAsFlow()

    private val _tankFillAll = tankFillDao.getTankFillData(carId)
    val tankFillAll: LiveData<List<TankFill>> = _tankFillAll

    private val _tankFillMapped = MutableLiveData<List<Pair<TankFill, Odometer?>>>()
    val tankFillMapped: LiveData<List<Pair<TankFill, Odometer?>>> = _tankFillMapped

    fun onDeleteTankFill(tankFill: TankFill) = viewModelScope.launch {
        tankFillDao.deleteTankFill(tankFill)
    }

    fun onUndoDeleteTankFill(tankFill: TankFill) = viewModelScope.launch {
        tankFillDao.addTankFill(tankFill)
    }

    fun setupTankFillData(list: List<TankFill>) = viewModelScope.launch {
        _tankFillMapped.value = list.map { tankFill ->
            val odometer = odometerDao.getOdometerById(tankFill.odometerId)
            Pair(tankFill, odometer)
        }
    }

    sealed class TankFilEvent {
        data class ShowUndoDeleteTankFillMessage(val tankFill: TankFill) : TankFilEvent()
        object ShowTankFillSavedConfirmationMessage : TankFilEvent()
    }
}
