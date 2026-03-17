package pl.kamilbaziak.carcostnotebook.ui.cardetails.details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import utils.CoroutineRule
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class DetailsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: DetailsViewModel
    private lateinit var carDao: CarDao
    private lateinit var odometerDao: OdometerDao
    private lateinit var tankFillDao: TankFillDao
    private lateinit var maintenanceDao: MaintenanceDao
    private val carId = 1L

    @Before
    fun setup() {
        carDao = mockk(relaxed = true)
        odometerDao = mockk(relaxed = true)
        tankFillDao = mockk(relaxed = true)
        maintenanceDao = mockk(relaxed = true)

        // Mock LiveData responses
        every { carDao.getCarById(carId) } returns MutableLiveData<Car?>()
        every { odometerDao.getOdometerLiveData(carId) } returns MutableLiveData<List<Odometer>>()
        every { tankFillDao.getTankFillLiveData(carId) } returns MutableLiveData<List<TankFill>>()
        every { maintenanceDao.getMaintenanceLiveData(carId) } returns MutableLiveData<List<Maintenance>>()

        viewModel = DetailsViewModel(carDao, odometerDao, tankFillDao, maintenanceDao, carId)
    }

    @Test
    fun `viewModel initializes with correct LiveData`() {
        // Then
        assertNotNull(viewModel.currentCarData)
        assertNotNull(viewModel.allOdometerData)
        assertNotNull(viewModel.allTankFillData)
        assertNotNull(viewModel.allMaintenance)
    }

    @Test
    fun `onCarEdit sends EditCar event with current car`() = runTest {
        // Given
        val car = Car(
            id = carId,
            brand = "Toyota",
            model = "Corolla",
            year = 2020,
            licensePlate = "ABC123",
            engineEnum = EngineEnum.Petrol,
            unit = UnitEnum.Kilometers,
            petrolUnit = PetrolUnitEnum.Liter,
            description = "Test car",
            currency = CurrencyEnum.Zloty
        )
        val carLiveData = MutableLiveData(car)
        every { carDao.getCarById(carId) } returns carLiveData

        val newViewModel = DetailsViewModel(carDao, odometerDao, tankFillDao, maintenanceDao, carId)

        // When
        newViewModel.onCarEdit()

        // Then
        val event = newViewModel.detailsViewEvent.first()
        assertIs<DetailsViewModel.DetailsViewEvent.EditCar>(event)
        assertEquals(car, event.car)
    }

    @Test
    fun `onCarDelete sends ShowCarDeleteDialogMessage event`() = runTest {
        // Given
        val car = Car(
            id = carId,
            brand = "Honda",
            model = "Civic",
            year = 2021,
            licensePlate = "XYZ789",
            engineEnum = EngineEnum.Diesel,
            unit = UnitEnum.Miles,
            petrolUnit = PetrolUnitEnum.Galon,
            description = "Another car",
            currency = CurrencyEnum.Euro
        )
        val carLiveData = MutableLiveData(car)
        every { carDao.getCarById(carId) } returns carLiveData

        val newViewModel = DetailsViewModel(carDao, odometerDao, tankFillDao, maintenanceDao, carId)

        // When
        newViewModel.onCarDelete()

        // Then
        val event = newViewModel.detailsViewEvent.first()
        assertIs<DetailsViewModel.DetailsViewEvent.ShowCarDeleteDialogMessage>(event)
        assertEquals(car, event.car)
    }

    @Test
    fun `deleteCar deletes car and all related data`() = runTest {
        // Given
        val car = Car(
            id = carId,
            brand = "Ford",
            model = "Focus",
            year = 2019,
            licensePlate = "DEF456",
            engineEnum = EngineEnum.Petrol,
            unit = UnitEnum.Kilometers,
            petrolUnit = PetrolUnitEnum.Liter,
            description = "Car to delete",
            currency = CurrencyEnum.Dolar
        )
        val carLiveData = MutableLiveData(car)
        every { carDao.getCarById(carId) } returns carLiveData

        val newViewModel = DetailsViewModel(carDao, odometerDao, tankFillDao, maintenanceDao, carId)

        // When
        newViewModel.deleteCar()
        coroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { carDao.deleteCar(car) }
        coVerify { odometerDao.deleteOdometer(carId) }
        coVerify { maintenanceDao.deleteMaintenance(carId) }
        coVerify { tankFillDao.deleteTankFill(carId) }

        val event = newViewModel.detailsViewEvent.first()
        assertIs<DetailsViewModel.DetailsViewEvent.CarDeleted>(event)
    }
}

