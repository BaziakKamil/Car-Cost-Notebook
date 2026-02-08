package pl.kamilbaziak.carcostnotebook.ui.cardetails.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.formatForText
import pl.kamilbaziak.carcostnotebook.name
import pl.kamilbaziak.carcostnotebook.shortcut
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits
import pl.kamilbaziak.carcostnotebook.ui.cardetails.petroltab.TankFillScreen
import pl.kamilbaziak.carcostnotebook.ui.compose.Screen

sealed class CarDetailsTab(val title: Int) {
    object Details : CarDetailsTab(R.string.details)
    object TankFill : CarDetailsTab(R.string.tank_fills)
    object Maintenance : CarDetailsTab(R.string.maintenance)
    object Odometer : CarDetailsTab(R.string.odometer)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CarDetailsScreen(
    carId: Long,
    navController: NavController,
    viewModel: DetailsViewModel = koinViewModel { parametersOf(carId) }
) {
    val car by viewModel.currentCarData.observeAsState()
    val allOdometerData by viewModel.allOdometerData.observeAsState()
    val allTankFillData by viewModel.allTankFillData.observeAsState()
    val allMaintenanceData by viewModel.allMaintenance.observeAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val tabs = listOf(
        CarDetailsTab.Details,
        CarDetailsTab.TankFill,
        CarDetailsTab.Maintenance,
        CarDetailsTab.Odometer
    )

    LaunchedEffect(Unit) {
        viewModel.detailsViewEvent.collectLatest {
            when (it) {
                is DetailsViewModel.DetailsViewEvent.ShowCarDeleteDialogMessage -> {
                    showDeleteDialog = true
                }
                is DetailsViewModel.DetailsViewEvent.CarDeleted -> {
                    navController.popBackStack()
                }
                is DetailsViewModel.DetailsViewEvent.EditCar -> {
                    navController.navigate(Screen.AddCar.createRoute(it.car?.id))
                }
                else -> {}
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.delete_dialog_title, car?.name() ?: "")) },
            text = { Text(text = stringResource(id = R.string.cannot_be_undone)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCar()
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = car?.name() ?: "") },
                actions = {
                    IconButton(onClick = { viewModel.onCarEdit() }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit))
                    }
                    IconButton(onClick = { viewModel.onCarDelete() }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(text = stringResource(id = tab.title)) }
                    )
                }
            }
            HorizontalPager(state = pagerState) {
                when (tabs[it]) {
                    CarDetailsTab.Details -> {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            car?.let { carData ->
                                TextField(value = carData.brand, onValueChange = {}, readOnly = true, label = { Text("Brand") }, modifier = Modifier.fillMaxWidth())
                                TextField(value = carData.model, onValueChange = {}, readOnly = true, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
                                TextField(value = carData.year.toString(), onValueChange = {}, readOnly = true, label = { Text("Year") }, modifier = Modifier.fillMaxWidth())
                                TextField(value = carData.licensePlate, onValueChange = {}, readOnly = true, label = { Text("License Plate") }, modifier = Modifier.fillMaxWidth())
                                TextField(value = carData.description.ifEmpty { "-" }, onValueChange = {}, readOnly = true, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                                TextField(value = carData.dateWhenBought?.toDate() ?: "-", onValueChange = {}, readOnly = true, label = { Text("Date When Bought") }, modifier = Modifier.fillMaxWidth())
                                TextField(value = carData.priceWhenBought?.let { price -> carData.currency.formatForText(context, price.toTwoDigits()) } ?: "-", onValueChange = {}, readOnly = true, label = { Text("Price When Bought") }, modifier = Modifier.fillMaxWidth())
                            }
                            allOdometerData?.let {
                                if (it.isNotEmpty()) {
                                    val totalDistance = it.maxOf { o -> o.input } - it.minOf { o -> o.input }
                                    TextField(value = "${totalDistance.toTwoDigits()} ${car?.unit?.shortcut()}", onValueChange = {}, readOnly = true, label = { Text("Total Distance Made") }, modifier = Modifier.fillMaxWidth())
                                }
                            }
                            allTankFillData?.let { tankFillData ->
                                val totalFuelConsumption = tankFillData.sumOf { it.quantity }.toTwoDigits()
                                val totalFuelCost = tankFillData.sumOf { it.petrolPrice?.times(it.quantity) ?: 0.0 }.toTwoDigits()
                                TextField(value = "$totalFuelConsumption ${car?.petrolUnit?.shortcut()}", onValueChange = {}, readOnly = true, label = { Text("Total Fuel Consumption") }, modifier = Modifier.fillMaxWidth())
                                TextField(value = car?.currency?.formatForText(context, totalFuelCost) ?: "-", onValueChange = {}, readOnly = true, label = { Text("Total Fuel Paid") }, modifier = Modifier.fillMaxWidth())
                            }
                            allMaintenanceData?.let { maintenanceData ->
                                val totalMaintenanceCost = maintenanceData.sumOf { it.price ?: 0.0 }.toTwoDigits()
                                TextField(value = car?.currency?.formatForText(context, totalMaintenanceCost) ?: "-", onValueChange = {}, readOnly = true, label = { Text("Total Maintenance Paid") }, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                    CarDetailsTab.TankFill -> TankFillScreen(carId = carId)
                    CarDetailsTab.Maintenance -> MaintenanceScreen()
                    CarDetailsTab.Odometer -> OdometerScreen()
                }
            }
        }
    }
}

@Composable
fun MaintenanceScreen() {
    Text(text = "Maintenance Screen")
}

@Composable
fun OdometerScreen() {
    Text(text = "Odometer Screen")
}
