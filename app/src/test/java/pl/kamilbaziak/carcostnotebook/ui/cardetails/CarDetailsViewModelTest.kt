package pl.kamilbaziak.carcostnotebook.ui.cardetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import pl.kamilbaziak.carcostnotebook.database.MaintenanceDao
import pl.kamilbaziak.carcostnotebook.database.OdometerDao
import pl.kamilbaziak.carcostnotebook.database.TankFillDao
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import utils.CoroutineRule
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(JUnit4::class)
class CarDetailsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: CarDetailsViewModel
    private lateinit var tankFillDao: TankFillDao
    private lateinit var odometerDao: OdometerDao
    private lateinit var maintenanceDao: MaintenanceDao
    private val carId = 1L

    @Before
    fun setup() {
        tankFillDao = mockk(relaxed = true)
        odometerDao = mockk(relaxed = true)
        maintenanceDao = mockk(relaxed = true)

        // Mock LiveData responses
        every { tankFillDao.getTankFillLiveData(carId) } returns MutableLiveData<List<TankFill>>()
        every { odometerDao.getOdometerLiveData(carId) } returns MutableLiveData<List<Odometer>>()
        every { maintenanceDao.getMaintenanceLiveData(carId) } returns MutableLiveData<List<Maintenance>>()

        viewModel = CarDetailsViewModel(tankFillDao, odometerDao, maintenanceDao, carId)
    }

    @Test
    fun `viewModel initializes with correct LiveData`() {
        // Then
        assertNotNull(viewModel.tankFillCount)
        assertNotNull(viewModel.odometerCount)
        assertNotNull(viewModel.maintenanceCount)
    }

    @Test
    fun `tankFillCount returns correct LiveData from DAO`() {
        // Given
        val tankFillLiveData = MutableLiveData<List<TankFill>>()
        every { tankFillDao.getTankFillLiveData(carId) } returns tankFillLiveData

        // When
        val newViewModel = CarDetailsViewModel(tankFillDao, odometerDao, maintenanceDao, carId)

        // Then
        assertEquals(tankFillLiveData, newViewModel.tankFillCount)
    }

    @Test
    fun `odometerCount returns correct LiveData from DAO`() {
        // Given
        val odometerLiveData = MutableLiveData<List<Odometer>>()
        every { odometerDao.getOdometerLiveData(carId) } returns odometerLiveData

        // When
        val newViewModel = CarDetailsViewModel(tankFillDao, odometerDao, maintenanceDao, carId)

        // Then
        assertEquals(odometerLiveData, newViewModel.odometerCount)
    }

    @Test
    fun `maintenanceCount returns correct LiveData from DAO`() {
        // Given
        val maintenanceLiveData = MutableLiveData<List<Maintenance>>()
        every { maintenanceDao.getMaintenanceLiveData(carId) } returns maintenanceLiveData

        // When
        val newViewModel = CarDetailsViewModel(tankFillDao, odometerDao, maintenanceDao, carId)

        // Then
        assertEquals(maintenanceLiveData, newViewModel.maintenanceCount)
    }
}

