package pl.kamilbaziak.carcostnotebook.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.name
import pl.kamilbaziak.carcostnotebook.ui.activity.MainActivityEvent
import pl.kamilbaziak.carcostnotebook.ui.activity.MainViewModel
import pl.kamilbaziak.carcostnotebook.ui.carlist.CarsListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarListScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    viewModel: CarsListViewModel = koinViewModel()
) {
    val cars by viewModel.carsMapped.observeAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.mainViewEvent.collectLatest {
            when (it) {
                is CarsListViewModel.MainViewEvent.NavigateToCarDetails -> {
                    navController.navigate(Screen.CarDetails.createRoute(it.car.id))
                }
                is CarsListViewModel.MainViewEvent.AddNewCar -> {
                    navController.navigate(Screen.AddCar.createRoute(null))
                }
                is CarsListViewModel.MainViewEvent.ShowCarEditDialogScreen -> {
                    navController.navigate(Screen.AddCar.createRoute(it.car.id))
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.mainViewModelEvents.collectLatest {
            when (it) {
                is MainActivityEvent.CarAdded -> {
                    // empty
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddNewCarClick() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_new_car)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(cars) { (car, odometer) ->
                CarListItem(
                    car = car,
                    odometer = odometer,
                    onCarClick = { viewModel.onCarClick(car) },
                    onCarEdit = { viewModel.onCarEdit(car) },
                    onCarDelete = { viewModel.onCarDelete(car) }
                )
            }
        }
    }
}

@Composable
fun CarListItem(
    car: Car,
    odometer: Odometer?,
    onCarClick: () -> Unit,
    onCarEdit: () -> Unit,
    onCarDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onCarClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = car.name())
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.edit)) },
                        onClick = {
                            onCarEdit()
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.delete)) },
                        onClick = {
                            onCarDelete()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}
