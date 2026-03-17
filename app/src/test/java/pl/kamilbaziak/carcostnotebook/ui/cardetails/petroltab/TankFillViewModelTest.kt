package pl.kamilbaziak.carcostnotebook.ui.cardetails.petroltab

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
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
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import pl.kamilbaziak.carcostnotebook.ui.cardetails.DataState
import utils.CoroutineRule
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class TankFillViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: TankFillViewModel
    private lateinit var tankFillDao: TankFillDao
    private lateinit var odometerDao: OdometerDao
    private val carId = 1L

    @Before
    fun setup() {
        tankFillDao = mockk(relaxed = true)
        odometerDao = mockk(relaxed = true)
        viewModel = TankFillViewModel(tankFillDao, odometerDao, carId)
    }

    @Test
    fun `prepareTankFillData with empty list sets NotFound state`() = runTest {
        // Given
        val emptyList = emptyList<TankFill>()

        // When
        viewModel.prepareTankFillData(emptyList)
        coroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(DataState.NotFound, viewModel.dataState.value)
    }

    @Test
    fun `prepareTankFillData with data sets Found state`() = runTest {
        // Given
        val tankFill = TankFill(
            id = 1,
            carId = carId,
            petrolEnum = PetrolEnum.Petrol,
            quantity = 50.0,
            petrolPrice = 6.5,
            distanceFromLastTankFill = 500.0,
            odometerId = 1L,
            computerReading = 7.5,
            petrolStation = "Shell",
            created = System.currentTimeMillis()
        )
        val odometer = Odometer(
            id = 1,
            carId = carId,
            input = 50000.0,
            unit = UnitEnum.Kilometers,
            created = System.currentTimeMillis(),
            canBeDeleted = false,
            description = "Tank fill"
        )
        val tankFillList = listOf(tankFill)

        coEvery { odometerDao.getOdometerById(1L) } returns odometer

        // When
        viewModel.prepareTankFillData(tankFillList)
        coroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.dataState.value
        assertIs<DataState.Found>(state)
        val data = state.list as List<Pair<TankFill, Odometer?>>
        assertEquals(1, data.size)
        assertEquals(tankFill, data[0].first)
        assertEquals(odometer, data[0].second)
    }

    @Test
    fun `onEditTankFill sends correct event`() = runTest {
        // Given
        val tankFill = TankFill(
            id = 1,
            carId = carId,
            petrolEnum = PetrolEnum.Diesel,
            quantity = 60.0,
            petrolPrice = 7.0,
            distanceFromLastTankFill = 600.0,
            odometerId = 2L,
            computerReading = 8.0,
            petrolStation = "BP",
            created = System.currentTimeMillis()
        )

        // When
        viewModel.onEditTankFill(tankFill)

        // Then
        val event = viewModel.tankFillEvent.first()
        assertIs<TankFillViewModel.TankFillEvent.ShowTankFillEditDialogScreen>(event)
        assertEquals(tankFill, event.tankFill)
    }

    @Test
    fun `onDeleteTankFill sets delete tank fill and sends event`() = runTest {
        // Given
        val tankFill = TankFill(
            id = 1,
            carId = carId,
            petrolEnum = PetrolEnum.Petrol,
            quantity = 50.0,
            petrolPrice = 6.5,
            distanceFromLastTankFill = 500.0,
            odometerId = 1L,
            computerReading = 7.5,
            petrolStation = "Shell",
            created = System.currentTimeMillis()
        )

        // When
        viewModel.onDeleteTankFill(tankFill)

        // Then
        val event = viewModel.tankFillEvent.first()
        assertIs<TankFillViewModel.TankFillEvent.ShowTankFillDeleteDialogMessage>(event)
    }
}

