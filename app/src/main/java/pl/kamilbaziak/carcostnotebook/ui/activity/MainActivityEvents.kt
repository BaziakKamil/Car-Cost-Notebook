package pl.kamilbaziak.carcostnotebook.ui.activity

import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.ui.newcar.AddNewCarFragment
import pl.kamilbaziak.carcostnotebook.ui.cardetails.CarDetailsFragment

sealed class MainActivityEvent(val tag: String) {

    data class OpenAddNewCar(
        val car: Car?,
        val title: String,
    ) : MainActivityEvent(AddNewCarFragment.TAG)

    data class OpenCarDetails(
        val car: Car,
        val title: String,
        val odometer: Odometer?,
    ) : MainActivityEvent(CarDetailsFragment.TAG)
}