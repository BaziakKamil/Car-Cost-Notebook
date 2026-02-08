package pl.kamilbaziak.carcostnotebook.ui.cardetails.petroltab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.model.TankFill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TankFillScreen(
    carId: Long,
    viewModel: TankFillViewModel = koinViewModel { parametersOf(carId) }
) {
    val tankFills by viewModel.tankFillData.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.tank_fills)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(tankFills) { tankFill ->
                TankFillListItem(tankFill)
            }
        }
    }
}

@Composable
fun TankFillListItem(tankFill: TankFill) {
    Column {
        Text(text = tankFill.created.toString())
    }
}
