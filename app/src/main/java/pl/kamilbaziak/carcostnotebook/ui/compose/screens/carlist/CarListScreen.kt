package pl.kamilbaziak.carcostnotebook.ui.compose.screens.carlist

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.ui.compose.components.CarListItem
import pl.kamilbaziak.carcostnotebook.ui.compose.components.NoCarView
import pl.kamilbaziak.carcostnotebook.ui.compose.model.CarUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarListScreen(cars: List<CarUiModel>) {
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