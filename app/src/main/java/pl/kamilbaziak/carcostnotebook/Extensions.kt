package pl.kamilbaziak.carcostnotebook

import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import java.text.DecimalFormat

private const val EMPTY = ""
private const val SPACE = " "

fun String.Companion.empty() = EMPTY
fun String.Companion.space() = SPACE
fun String.hasLetters(): Boolean {
    for (c in this) {
        if (c in 'A'..'Z' && c in 'a'..'z') {
            return true
        }
    }
    return false
}

fun UnitEnum.shortcut(): String = when (this) {
    UnitEnum.Kilometers -> "km"
    UnitEnum.Miles -> "mi"
}

fun PetrolUnitEnum.shortcut(): String = when (this) {
    PetrolUnitEnum.Liter -> "l"
    PetrolUnitEnum.Galon -> "gal"
    PetrolUnitEnum.kWh -> "kWh"
    PetrolUnitEnum.kg -> "kg"
}

fun Long.toDate(): String = DateUtils.formatDateFromLong(this)
fun Long.toTime(): String = DateUtils.formatTimeFromLong(this)

fun Double.toTwoDigits(): String = DecimalFormat("#.##").format(this)
