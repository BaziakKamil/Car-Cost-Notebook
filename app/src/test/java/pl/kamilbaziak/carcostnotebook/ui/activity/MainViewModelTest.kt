package pl.kamilbaziak.carcostnotebook.ui.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import utils.CoroutineRule
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel()
    }

    @Test
    fun `openAddNewCar emits correct event`() = runTest {
        // Given
        val title = "Add New Car"
        val car = null

        // When
        viewModel.openAddNewCar(title, car)

        // Then
        val event = viewModel.mainViewModelEvents.first()
        assertIs<MainActivityEvent.OpenAddNewCar>(event)
        assertEquals(title, event.title)
        assertEquals(car, event.car)
    }

    @Test
    fun `openAddNewCar with existing car emits correct event`() = runTest {
        // Given
        val title = "Edit Car"
        val car = Car(
            id = 1,
            brand = "Toyota",
            model = "Corolla",
            year = 2020,
            licensePlate = "ABC123",
            engineEnum = pl.kamilbaziak.carcostnotebook.enums.EngineEnum.Petrol,
            unit = pl.kamilbaziak.carcostnotebook.enums.UnitEnum.Kilometers,
            petrolUnit = pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum.Liter,
            description = "Test car",
            currency = pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum.Zloty,
            dateWhenBought = System.currentTimeMillis()
        )

        // When
        viewModel.openAddNewCar(title, car)

        // Then
        val event = viewModel.mainViewModelEvents.first()
        assertIs<MainActivityEvent.OpenAddNewCar>(event)
        assertEquals(title, event.title)
        assertEquals(car, event.car)
    }

    @Test
    fun `openCarDetails emits correct event`() = runTest {
        // Given
        val title = "Car Details"
        val car = Car(
            id = 1,
            brand = "Honda",
            model = "Civic",
            year = 2021,
            licensePlate = "XYZ789",
            engineEnum = pl.kamilbaziak.carcostnotebook.enums.EngineEnum.Diesel,
            unit = pl.kamilbaziak.carcostnotebook.enums.UnitEnum.Kilometers,
            petrolUnit = pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum.Liter,
            description = "Another test car",
            currency = pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum.Euro,
            dateWhenBought = System.currentTimeMillis()
        )
        val odometer = Odometer(
            id = 1,
            carId = 1,
            input = 50000.0,
            unit = pl.kamilbaziak.carcostnotebook.enums.UnitEnum.Kilometers,
            created = System.currentTimeMillis(),
            canBeDeleted = true,
            description = "Current odometer"
        )

        // When
        viewModel.openCarDetails(title, car, odometer)

        // Then
        val event = viewModel.mainViewModelEvents.first()
        assertIs<MainActivityEvent.OpenCarDetails>(event)
        assertEquals(title, event.title)
        assertEquals(car, event.car)
        assertEquals(odometer, event.odometer)
    }

    @Test
    fun `openCarDetails with null odometer emits correct event`() = runTest {
        // Given
        val title = "Car Details"
        val car = Car(
            id = 2,
            brand = "Ford",
            model = "Focus",
            year = 2019,
            licensePlate = "DEF456",
            engineEnum = pl.kamilbaziak.carcostnotebook.enums.EngineEnum.Petrol,
            unit = pl.kamilbaziak.carcostnotebook.enums.UnitEnum.Miles,
            petrolUnit = pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum.Galon,
            description = "Third test car",
            currency = pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum.Dolar,
            dateWhenBought = System.currentTimeMillis()
        )

        // When
        viewModel.openCarDetails(title, car, null)

        // Then
        val event = viewModel.mainViewModelEvents.first()
        assertIs<MainActivityEvent.OpenCarDetails>(event)
        assertEquals(title, event.title)
        assertEquals(car, event.car)
        assertEquals(null, event.odometer)
    }
}

