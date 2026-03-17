package pl.kamilbaziak.carcostnotebook.ui.cardetails.odometertab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.ui.cardetails.DataState
import utils.CoroutineRule
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class OdometerViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: OdometerViewModel
    private lateinit var odometerDao: OdometerDao
    private val carId = 1L

    @Before
    fun setup() {
        odometerDao = mockk(relaxed = true)
        viewModel = OdometerViewModel(odometerDao, carId)
    }

    @Test
    fun `prepareOdometerData with empty list sets NotFound state`() {
        // Given
        val emptyList = emptyList<Odometer>()

        // When
        viewModel.prepareOdometerData(emptyList)

        // Then
        assertEquals(DataState.NotFound, viewModel.dataState.value)
    }

    @Test
    fun `prepareOdometerData with data sets Found state and sorts by created desc`() {
        // Given
        val odometer1 = Odometer(
            id = 1,
            carId = carId,
            input = 50000.0,
            unit = UnitEnum.Kilometers,
            created = 1000L,
            canBeDeleted = true,
            description = "First reading"
        )
        val odometer2 = Odometer(
            id = 2,
            carId = carId,
            input = 55000.0,
            unit = UnitEnum.Kilometers,
            created = 2000L,
            canBeDeleted = true,
            description = "Second reading"
        )
        val odometerList = listOf(odometer1, odometer2)

        // When
        viewModel.prepareOdometerData(odometerList)

        // Then
        val state = viewModel.dataState.value
        assertIs<DataState.Found>(state)
        val data = state.list as List<Odometer>
        assertEquals(2, data.size)
        // Should be sorted by created descending
        assertEquals(odometer2, data[0])
        assertEquals(odometer1, data[1])
    }

    @Test
    fun `onEditOdometer sends correct event`() = runTest {
        // Given
        val odometer = Odometer(
            id = 1,
            carId = carId,
            input = 60000.0,
            unit = UnitEnum.Kilometers,
            created = System.currentTimeMillis(),
            canBeDeleted = true,
            description = "Manual reading"
        )

        // When
        viewModel.onEditOdometer(odometer)

        // Then
        val event = viewModel.odometerEvent.first()
        assertIs<OdometerViewModel.OdometerEvent.ShowOdometerEditDialogScreen>(event)
        assertEquals(odometer, event.odometer)
    }

    @Test
    fun `onDeleteOdometer sets delete odometer and sends event`() = runTest {
        // Given
        val odometer = Odometer(
            id = 1,
            carId = carId,
            input = 65000.0,
            unit = UnitEnum.Miles,
            created = System.currentTimeMillis(),
            canBeDeleted = true,
            description = "To be deleted"
        )

        // When
        viewModel.onDeleteOdometer(odometer)

        // Then
        val event = viewModel.odometerEvent.first()
        assertIs<OdometerViewModel.OdometerEvent.ShowOdometerDeleteDialogMessage>(event)
    }

    @Test
    fun `deleteOdometer deletes odometer and sends undo event`() = runTest {
        // Given
        val odometer = Odometer(
            id = 1,
            carId = carId,
            input = 70000.0,
            unit = UnitEnum.Kilometers,
            created = System.currentTimeMillis(),
            canBeDeleted = true,
            description = "Delete test"
        )

        // First set the odometer to delete
        viewModel.onDeleteOdometer(odometer)
        viewModel.odometerEvent.first() // Consume the delete dialog event

        // When
        viewModel.deleteOdometer()
        coroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { odometerDao.deleteOdometer(odometer) }

        val event = viewModel.odometerEvent.first()
        assertIs<OdometerViewModel.OdometerEvent.ShowUndoDeleteOdometerMessage>(event)
        assertEquals(odometer, event.odometer)
    }

    @Test
    fun `onUndoDeleteOdometer adds odometer back`() = runTest {
        // Given
        val odometer = Odometer(
            id = 1,
            carId = carId,
            input = 75000.0,
            unit = UnitEnum.Kilometers,
            created = System.currentTimeMillis(),
            canBeDeleted = true,
            description = "Undo test"
        )

        // When
        viewModel.onUndoDeleteOdometer(odometer)
        coroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { odometerDao.addOdometer(odometer) }
    }
}

