package pl.kamilbaziak.carcostnotebook.ui.cardetails.maintenancetab

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
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.ui.cardetails.DataState
import utils.CoroutineRule
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MaintenanceViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: MaintenanceViewModel
    private lateinit var maintenanceDao: MaintenanceDao
    private lateinit var odometerDao: OdometerDao
    private val carId = 1L

    @Before
    fun setup() {
        maintenanceDao = mockk(relaxed = true)
        odometerDao = mockk(relaxed = true)
        viewModel = MaintenanceViewModel(maintenanceDao, odometerDao, carId)
    }

    @Test
    fun `prepareMaintenanceData with empty list sets NotFound state`() {
        // Given
        val emptyList = emptyList<Maintenance>()

        // When
        viewModel.prepareMaintenanceData(emptyList)

        // Then
        assertEquals(DataState.NotFound, viewModel.dataState.value)
    }

    @Test
    fun `prepareMaintenanceData with data sets Found state and sorts by created desc`() {
        // Given
        val maintenance1 = Maintenance(
            id = 1,
            carId = carId,
            name = "Oil change",
            price = 200.0,
            odometerId = 1L,
            created = 1000L,
            dueDate = null,
            notifyWhenDue = false,
            description = "Regular oil change"
        )
        val maintenance2 = Maintenance(
            id = 2,
            carId = carId,
            name = "Tire rotation",
            price = 100.0,
            odometerId = 2L,
            created = 2000L,
            dueDate = null,
            notifyWhenDue = false,
            description = "Rotate tires"
        )
        val maintenanceList = listOf(maintenance1, maintenance2)

        // When
        viewModel.prepareMaintenanceData(maintenanceList)

        // Then
        val state = viewModel.dataState.value
        assertIs<DataState.Found>(state)
        val data = state.list as List<Maintenance>
        assertEquals(2, data.size)
        // Should be sorted by created descending
        assertEquals(maintenance2, data[0])
        assertEquals(maintenance1, data[1])
    }

    @Test
    fun `onEditMaintenance sends correct event`() = runTest {
        // Given
        val maintenance = Maintenance(
            id = 1,
            carId = carId,
            name = "Brake pads",
            price = 300.0,
            odometerId = 1L,
            created = System.currentTimeMillis(),
            dueDate = null,
            notifyWhenDue = false,
            description = "Replace brake pads"
        )

        // When
        viewModel.onEditMaintenance(maintenance)

        // Then
        val event = viewModel.maintenanceEvent.first()
        assertIs<MaintenanceViewModel.MaintenanceEvent.ShowCarEditDialogScreen>(event)
        assertEquals(maintenance, event.maintenance)
    }

    @Test
    fun `onDeleteMaintenance sets delete maintenance and sends event`() = runTest {
        // Given
        val maintenance = Maintenance(
            id = 1,
            carId = carId,
            name = "Air filter",
            price = 50.0,
            odometerId = null,
            created = System.currentTimeMillis(),
            dueDate = null,
            notifyWhenDue = false,
            description = "Replace air filter"
        )

        // When
        viewModel.onDeleteMaintenance(maintenance)

        // Then
        val event = viewModel.maintenanceEvent.first()
        assertIs<MaintenanceViewModel.MaintenanceEvent.ShowMaintenanceDeleteDialogMessage>(event)
        assertEquals(maintenance, event.maintenance)
    }

    @Test
    fun `deleteMaintenance deletes maintenance and paired odometer`() = runTest {
        // Given
        val odometer = Odometer(
            id = 1,
            carId = carId,
            input = 50000.0,
            unit = UnitEnum.Kilometers,
            created = System.currentTimeMillis(),
            canBeDeleted = false,
            description = "Maintenance"
        )
        val maintenance = Maintenance(
            id = 1,
            carId = carId,
            name = "Service",
            price = 500.0,
            odometerId = 1L,
            created = System.currentTimeMillis(),
            dueDate = null,
            notifyWhenDue = false,
            description = "Full service"
        )

        coEvery { odometerDao.getOdometerById(1L) } returns odometer

        // First set the maintenance to delete
        viewModel.onDeleteMaintenance(maintenance)
        viewModel.maintenanceEvent.first() // Consume the event

        // When
        viewModel.deleteMaintenance()
        coroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { maintenanceDao.deleteMaintenance(maintenance) }
        coVerify { odometerDao.deleteOdometer(odometer) }
    }
}

