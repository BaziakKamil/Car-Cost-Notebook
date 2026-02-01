package pl.kamilbaziak.carcostnotebook.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.ui.compose.components.NoCarView
import pl.kamilbaziak.carcostnotebook.ui.compose.theme.CarCostNotebookTheme

data class Car(
    val name: String,
    val brand: String,
    val year: Int,
    val licensePlate: String,
    val odometer: Int,
    val description: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarCostNotebookTheme {
                CarListScreen(
                    cars = listOf(
                        Car("A4", "Audi", 2005, "KN 45673", 300900, "Description"),
                        Car("E46", "BMW", 2003, "KR 12345", 250000, "Another description")
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarListScreen(cars: List<Car>) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isCollapsed = scrollBehavior.state.collapsedFraction > 0.5
    val iconSize by animateDpAsState(targetValue = if (isCollapsed) 32.dp else 48.dp)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.carcostnotebook_logo_small),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .padding(vertical = 4.dp)
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Handle FAB click */ }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_new_car))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (cars.isEmpty()) {
                NoCarView()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(cars) { car ->
                        CarListItem(car)
                    }
                }
            }
        }
    }
}

@Composable
fun CarListItem(car: Car) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(text = car.brand, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = car.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${car.odometer} km", modifier = Modifier.weight(1f))
                    Text(text = car.year.toString(), modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = car.licensePlate,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp)
                    )
                }
                Text(
                    text = car.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewOl() {
    CarCostNotebookTheme {
        CarListScreen(
            cars = listOf(
                Car("A4", "Audi", 2005, "KN 45673", 300900, "Description"),
                Car("E46", "BMW", 2003, "KR 12345", 250000, "Another description"),
                Car("Golf", "Volkswagen", 2010, "AB 12345", 150000, "Compact car"),
                Car("Civic", "Honda", 2018, "CD 67890", 80000, "Reliable sedan"),
                Car("Mustang", "Ford", 2015, "EF 54321", 120000, "Muscle car"),
                Car("Corolla", "Toyota", 2019, "GH 98765", 60000, "Best selling car"),
                Car("CX-5", "Mazda", 2017, "IJ 13579", 95000, "Stylish SUV"),
                Car("Outback", "Subaru", 2020, "KL 24680", 40000, "All-wheel drive wagon"),
                Car("Model 3", "Tesla", 2021, "MN 11223", 25000, "Electric car"),
                Car("Wrangler", "Jeep", 2016, "OP 44556", 110000, "Off-road vehicle"),
                Car("Soul", "Kia", 2019, "QR 77889", 75000, "Quirky hatchback"),
                Car("Astra", "Opel", 2012, "ST 99001", 180000, "European compact"),
                Car("Clio", "Renault", 2014, "UV 22334", 140000, "French supermini"),
                Car("500", "Fiat", 2013, "WX 55667", 130000, "City car"),
                Car("XC90", "Volvo", 2018, "YZ 88990", 90000, "Safe and spacious SUV"),
                Car("C-Class", "Mercedes-Benz", 2017, "BC 11122", 100000, "Luxury sedan"),
                Car("X5", "BMW", 2019, "DE 33344", 85000, "Sporty SUV"),
                Car("Q5", "Audi", 2018, "FG 55566", 92000, "Premium compact SUV"),
                Car("Passat", "Volkswagen", 2016, "HI 77788", 125000, "Family sedan"),
                Car("Accord", "Honda", 2020, "JK 99900", 55000, "Mid-size sedan"),
                Car("Camry", "Toyota", 2021, "LM 12121", 35000, "Dependable family car"),
                Car("F-150", "Ford", 2019, "NO 34343", 70000, "Pickup truck")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyListPreview() {
    CarCostNotebookTheme {
        CarListScreen(cars = emptyList())
    }
}
