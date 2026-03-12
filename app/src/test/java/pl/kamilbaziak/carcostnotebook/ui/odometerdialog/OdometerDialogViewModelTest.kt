package pl.kamilbaziak.carcostnotebook.ui.odometerdialog


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import utils.CoroutineRule
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class OdometerDialogViewModelTest {

    private lateinit var viewModel: OdometerDialogViewModel
    private lateinit var odometerDao: OdometerDao
    private lateinit var carDao: CarDao

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = CoroutineRule()

    @Before
    fun setup() {
        odometerDao = mockk()
        carDao = mockk()

        viewModel = OdometerDialogViewModel(odometerDao, carDao, 1L)
    }

    @Test
    fun `test changePickedDate`() {
        val observer = mockk<Observer<Long>>(relaxed = true)
        viewModel.pickedDate.observeForever(observer)

        val newDate = Date().time + 1000
        viewModel.changePickedDate(newDate)

        verify { observer.onChanged(newDate) }
    }

    @Test
    fun `test updateOdometer`() = runTest {
        val odometer = Odometer(1, 1L, 100.0, UnitEnum.Kilometers, Date().time, true, null)
        val newOdometerValue = 200.0
        val description = "Test description"

        coEvery { odometerDao.updateOdometer(any()) } just runs

        viewModel.updateOdometer(newOdometerValue, description, odometer)
        advanceUntilIdle()

        coVerify {
            odometerDao.updateOdometer(
                match {
                    it.id == odometer.id &&
                    it.carId == odometer.carId &&
                    it.input == newOdometerValue &&
                    it.unit == odometer.unit &&
                    it.canBeDeleted == odometer.canBeDeleted &&
                    it.description == description
                }
            )
        }
    }

    @Test
    fun `test addOdometer`() = runTest {
        val carId = 1L
        val odometerValue = 100.0
        val description = "Test description"
        val car = Car(
            id = carId,
            brand = "Toyota",
            model = "Corolla",
            year = 2020,
            licensePlate = "ABC123",
            engineEnum = EngineEnum.Petrol,
            petrolUnit = PetrolUnitEnum.Liter,
            unit = UnitEnum.Kilometers,
            description = "Test car",
            currency = CurrencyEnum.Zloty
        )

        coEvery { carDao.getCarByIdSuspend(carId) } returns car
        coEvery { odometerDao.addOdometer(any()) } returns 1L

        viewModel.addOdometer(odometerValue, description)
        advanceUntilIdle()

        coVerify {
            odometerDao.addOdometer(
                match {
                    it.carId == carId &&
                    it.input == odometerValue &&
                    it.unit == UnitEnum.Kilometers &&
                    it.canBeDeleted == true &&
                    it.description == description
                }
            )
        }
    }
}
