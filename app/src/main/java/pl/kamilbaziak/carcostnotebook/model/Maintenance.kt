package pl.kamilbaziak.carcostnotebook.model

import java.util.*

data class Maintenance(
    val id: Int,
    val name: String,
    val odometer: String,
    val date: Date,
    val dueDate: Date,
    val description: String
)
