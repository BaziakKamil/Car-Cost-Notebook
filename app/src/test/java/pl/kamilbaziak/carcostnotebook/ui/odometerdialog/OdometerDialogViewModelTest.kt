package pl.kamilbaziak.carcostnotebook.ui.odometerdialog


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import io.mockk.awaits
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import pl.kamilbaziak.carcostnotebook.database.CarDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.di.viewModelsModule
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import utils.CoroutineRule
import java.util.Date

@RunWith(JUnit4::class)
class OdometerDialogViewModelTest : KoinTest {

    private var viewModel: OdometerDialogViewModel = mockk()
    private var odometerDao: OdometerDao = mockk()
    private lateinit var carDao: CarDao

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = CoroutineRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(viewModelsModule)
    }

    @Before
    fun setup() {
        odometerDao = mockk()
        carDao = mockk()

        viewModel = OdometerDialogViewModel(odometerDao, carDao, 1L)
    }

//    @After
//    fun tearDown() {
//        testScope.cleanupTestCoroutines()
//    }

    @Test
    fun `test changePickedDate`() {
        val observer = mockk<Observer<Long>>(relaxed = true)
        viewModel.pickedDate.observeForever(observer)

        val newDate = Date().time + 1000
        viewModel.changePickedDate(newDate)

        verify { observer.onChanged(newDate) }
    }

    @Test
    fun `test updateOdometer`() {
        val odometer = Odometer(1, 1L, 100.0, UnitEnum.Kilometers, Date().time, true, null)
        val newOdometerValue = 200.0
        val description = "Test description"

        coEvery { odometerDao.updateOdometer(any()) } just runs

        viewModel.updateOdometer(newOdometerValue, description, odometer)

        coVerify {
            odometerDao.updateOdometer(
                odometer.copy(
                    input = newOdometerValue,
                    created = any(),
                    description = description
                )
            )
        }
    }

    @Test
    fun `test addOdometer`() {
        val carId = 1L
        val odometerValue = 100.0
        val description = "Test description"
        val car = mockk<Car>(relaxed = true)

        every { carDao.getCarById(carId) } returns MutableLiveData(car)
        coEvery { odometerDao.addOdometer(any()) } just awaits

        viewModel.addOdometer(odometerValue, description)

        coVerify {
            odometerDao.addOdometer(
                Odometer(
                    0,
                    carId = carId,
                    input = odometerValue,
                    unit = car.unit,
                    created = any(),
                    canBeDeleted = true,
                    description = description
                )
            )
        }
    }
}
