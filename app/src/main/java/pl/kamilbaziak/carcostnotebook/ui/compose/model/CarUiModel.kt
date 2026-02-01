package pl.kamilbaziak.carcostnotebook.ui.compose.model

data class CarUiModel(
    val name: String,
    val brand: String,
    val year: Int,
    val licensePlate: String,
    val odometer: Int,
    val description: String
)
