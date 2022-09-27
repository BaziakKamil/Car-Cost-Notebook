package pl.kamilbaziak.carcostnotebook.model

import pl.kamilbaziak.carcostnotebook.enums.Petrol
import java.util.*

data class PetrolInsert(
    val odometer: Int,
    val petrol: Petrol,
    val quantity: Int,
    val computerReading: Double,
    val petroStation: String,
    val date: Date
)
