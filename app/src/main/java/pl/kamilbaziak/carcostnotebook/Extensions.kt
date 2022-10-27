package pl.kamilbaziak.carcostnotebook

import pl.kamilbaziak.carcostnotebook.enums.UnitEnum

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
fun UnitEnum.shortcut(): String = when(this) {
    UnitEnum.Kilometers -> "km"
    UnitEnum.Miles -> "mi"
}
