package pl.kamilbaziak.carcostnotebook.ui.compose.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.koin.androidx.viewmodel.ext.android.getViewModel
import pl.kamilbaziak.carcostnotebook.ui.activity.MainViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetails.details.CarDetailsScreen
import pl.kamilbaziak.carcostnotebook.ui.compose.CarListScreen
import pl.kamilbaziak.carcostnotebook.ui.compose.Screen
import pl.kamilbaziak.carcostnotebook.ui.compose.theme.CarCostNotebookTheme
import pl.kamilbaziak.carcostnotebook.ui.newcar.NewCarScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarCostNotebookTheme {
                val navController = rememberNavController()
                val mainViewModel: MainViewModel = getViewModel()
                NavHost(navController = navController, startDestination = Screen.CarList.route) {
                    composable(Screen.CarList.route) {
                        CarListScreen(navController = navController, mainViewModel = mainViewModel)
                    }
                    composable(
                        route = Screen.AddCar.route,
                        arguments = listOf(navArgument("carId") { 
                            type = NavType.LongType
                            defaultValue = -1L
                        })
                    ) { backStackEntry ->
                        NewCarScreen(
                            navController = navController,
                            carId = backStackEntry.arguments?.getLong("carId"),
                            mainViewModel = mainViewModel
                        )
                    }
                    composable(
                        route = Screen.CarDetails.route,
                        arguments = listOf(navArgument("carId") { 
                            type = NavType.LongType
                            defaultValue = -1L
                        })
                    ) { backStackEntry ->
                        val carId = backStackEntry.arguments?.getLong("carId")
                        if (carId != null && carId != -1L) {
                            CarDetailsScreen(
                                navController = navController,
                                carId = carId
                            )
                        }
                    }
                }
            }
        }
    }
}
