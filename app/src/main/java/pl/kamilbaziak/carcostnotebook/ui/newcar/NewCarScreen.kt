package pl.kamilbaziak.carcostnotebook.ui.newcar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCarScreen(
    navController: NavController,
    carId: Long?,
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
    var unitError by remember { mutableStateOf<String?>(null) }
    var engineError by remember { mutableStateOf<String?>(null) }
    var petrolError by remember { mutableStateOf<String?>(null) }
    var currencyError by remember { mutableStateOf<String?>(null) }

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
                title = { Text(text = if (carId == -1L) stringResource(id = R.string.add_new_car) else stringResource(id = R.string.edit_car)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") }, isError = brandError != null, supportingText = { brandError?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
            TextField(value = model, onValueChange = { model = it }, label = { Text("Model") }, isError = modelError != null, supportingText = { modelError?.let { Text(it) } }, modifier = Modifier.fillMaxWidth())
            TextField(value = year, onValueChange = { year = it }, label = { Text("Year") }, isError = yearError != null, supportingText = { yearError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            TextField(value = licensePlate, onValueChange = { licensePlate = it }, label = { Text("License Plate") }, modifier = Modifier.fillMaxWidth())
            TextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            TextField(value = priceWhenBought, onValueChange = { priceWhenBought = it }, label = { Text("Price When Bought") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Button(onClick = { showDatePicker = true }) {
                Text(text = dateWhenBought?.toDate() ?: stringResource(id = R.string.choose_date_when_bought))
            }
            if (car == null) {
                TextField(value = odometer, onValueChange = { odometer = it }, label = { Text("Odometer") }, isError = odometerError != null, supportingText = { odometerError?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                TextField(value = odometerWhenBought, onValueChange = { odometerWhenBought = it }, label = { Text("Odometer When Bought") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            }
            EnumDropDown(items = EngineEnum.values().toList(), selectedItem = engineType, onItemSelected = { engineType = it }, label = "Engine Type", isError = engineError != null, errorText = engineError)
            EnumDropDown(items = PetrolUnitEnum.values().toList(), selectedItem = petrolUnit, onItemSelected = { petrolUnit = it }, label = "Petrol Unit", isError = petrolError != null, errorText = petrolError)
            EnumDropDown(items = UnitEnum.values().toList(), selectedItem = unit, onItemSelected = { unit = it }, label = "Unit", isError = unitError != null, errorText = unitError)
            EnumDropDown(items = CurrencyEnum.values().toList(), selectedItem = currency, onItemSelected = { currency = it }, label = "Currency", isError = currencyError != null, errorText = currencyError)
            Button(onClick = { 
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
            }) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> EnumDropDown(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    label: String,
    isError: Boolean,
    errorText: String?
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
        TextField(
            value = selectedItem.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            isError = isError,
            supportingText = { errorText?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth().menuAnchor()
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
