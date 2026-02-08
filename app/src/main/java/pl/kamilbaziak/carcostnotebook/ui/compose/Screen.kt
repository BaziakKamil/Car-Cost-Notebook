package pl.kamilbaziak.carcostnotebook.ui.compose

sealed class Screen(val route: String) {
    object CarList : Screen("car_list")
    object AddCar : Screen("add_car?carId={carId}") {
        fun createRoute(carId: Long?) = "add_car?carId=${carId ?: -1L}"
    }
    object CarDetails : Screen("car_details/{carId}") {
        fun createRoute(carId: Long) = "car_details/$carId"
    }
}