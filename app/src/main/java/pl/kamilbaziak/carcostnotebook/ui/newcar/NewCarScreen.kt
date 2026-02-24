package pl.kamilbaziak.carcostnotebook.ui.newcar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.ui.activity.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCarScreen(
    navController: NavController,
    carId: Long?,
    mainViewModel: MainViewModel,
    viewModel: AddNewCarViewModel = koinViewModel { parametersOf(carId) }
) {
    val car by viewModel.car.observeAsState()
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceWhenBought by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var odometerWhenBought by remember { mutableStateOf("") }
    val dateWhenBought by viewModel.pickedDate.observeAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var engineType by remember { mutableStateOf(EngineEnum.Diesel) }
    var petrolUnit by remember { mutableStateOf(PetrolUnitEnum.Liter) }
    var unit by remember { mutableStateOf(UnitEnum.Kilometers) }
    var currency by remember { mutableStateOf(CurrencyEnum.Zloty) }

    var brandError by remember { mutableStateOf<String?>(null) }
    var modelError by remember { mutableStateOf<String?>(null) }
    var yearError by remember { mutableStateOf<String?>(null) }
    var odometerError by remember { mutableStateOf<String?>(null) }

    val onSave = {
        brandError = if (brand.isEmpty()) "Cannot be empty" else null
        modelError = if (model.isEmpty()) "Cannot be empty" else null
        yearError = if (year.isEmpty()) "Cannot be empty" else null
        odometerError = if (odometer.isEmpty() && car == null) "Cannot be empty" else null

        if (brand.isNotEmpty() && model.isNotEmpty() && year.isNotEmpty() && (odometer.isNotEmpty() || car != null)) {
            if (car == null) {
                viewModel.addCar(
                    Car(
                        0,
                        brand,
                        model,
                        year.toInt(),
                        licensePlate,
                        engineType,
                        petrolUnit,
                        unit,
                        description,
                        priceWhenBought.toDoubleOrNull(),
                        dateWhenBought,
                        currency
                    ),
                    odometer.toDouble(),
                    odometerWhenBought.toDoubleOrNull()
                )
            } else {
                viewModel.updateCar(
                    car!!.copy(
                        brand = brand,
                        model = model,
                        year = year.toInt(),
                        licensePlate = licensePlate,
                        description = description,
                        priceWhenBought = priceWhenBought.toDoubleOrNull(),
                        dateWhenBought = dateWhenBought
                    )
                )
            }
        }
    }

    LaunchedEffect(car) {
        car?.let {
            brand = it.brand
            model = it.model
            year = it.year.toString()
            licensePlate = it.licensePlate
            description = it.description
            priceWhenBought = it.priceWhenBought?.toString() ?: ""
            engineType = it.engineEnum
            petrolUnit = it.petrolUnit
            unit = it.unit
            currency = it.currency
            viewModel.changePickedDate(it.dateWhenBought ?: System.currentTimeMillis())
        }
    }

    LaunchedEffect(Unit) {
        viewModel.addNewCarEvent.collectLatest {
            when (it) {
                AddNewCarViewModel.AddNewCarEvent.NavigateBack -> navController.popBackStack()
                AddNewCarViewModel.AddNewCarEvent.CarAdded -> mainViewModel.onCarAdded()
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    datePickerState.selectedDateMillis?.let {
                        viewModel.changePickedDate(it)
                    }
                    showDatePicker = false 
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (carId == -1L) stringResource(id = R.string.add_new_car) else stringResource(id = R.string.edit_car)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionTitle(title = stringResource(id = R.string.car_data))
            OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text(stringResource(R.string.car_brand_title)) }, isError = brandError != null, supportingText = { brandError?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text(stringResource(R.string.car_model_title)) }, isError = modelError != null, supportingText = { modelError?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text(stringResource(R.string.production_year_title)) }, isError = yearError != null, supportingText = { yearError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = it }, label = { Text(stringResource(R.string.license_plate)) }, modifier = Modifier.weight(1f))
            }
            Column(
                modifier = Modifier.border(1.dp, Color.Gray, shape = MaterialTheme.shapes.medium).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionTitle(title = stringResource(id = R.string.non_editable_data))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = odometer, onValueChange = { odometer = it }, label = { Text(stringResource(R.string.odometer_title)) }, isError = odometerError != null, supportingText = { odometerError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), enabled = car == null)
                    EnumDropDown(items = UnitEnum.values().toList(), selectedItem = unit, onItemSelected = { unit = it }, label = stringResource(R.string.odometer_unit_title), modifier = Modifier.weight(1f), enabled = car == null)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EnumDropDown(items = EngineEnum.values().toList(), selectedItem = engineType, onItemSelected = { engineType = it }, label = stringResource(R.string.petrol_type_title), modifier = Modifier.weight(1f), enabled = car == null)
                    EnumDropDown(items = PetrolUnitEnum.values().toList(), selectedItem = petrolUnit, onItemSelected = { petrolUnit = it }, label = stringResource(R.string.petrol_unit_title), modifier = Modifier.weight(1f), enabled = car == null)
                }
                EnumDropDown(items = CurrencyEnum.values().toList(), selectedItem = currency, onItemSelected = { currency = it }, label = stringResource(R.string.currency_title), enabled = car == null, modifier = Modifier.fillMaxWidth())
            }
            SectionTitle(title = stringResource(id = R.string.car_data_when_bought))
            OutlinedTextField(
                value = dateWhenBought?.toDate() ?: "",
                onValueChange = { },
                label = { Text(stringResource(id = R.string.choose_date_when_bought)) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            )
            OutlinedTextField(value = priceWhenBought, onValueChange = { priceWhenBought = it }, label = { Text(stringResource(R.string.car_price_when_bought)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = odometerWhenBought, onValueChange = { odometerWhenBought = it }, label = { Text(stringResource(R.string.odometer_when_bought)) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Divider(modifier = Modifier.weight(1f))
        Text(text = title, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 8.dp))
        Divider(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> EnumDropDown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
        OutlinedTextField(
            value = selectedItem.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = modifier.menuAnchor(),
            enabled = enabled
        )
        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            items.forEach {
                DropdownMenuItem(text = { Text(it.name) }, onClick = {
                    onItemSelected(it)
                    isExpanded = false
                })
            }
        }
    }
}
