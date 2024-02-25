package pl.kamilbaziak.carcostnotebook

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import pl.kamilbaziak.carcostnotebook.enums.CurrencyEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car
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

fun <T> Fragment.extra(key: String) = lazy { arguments?.get(key) as T }

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

fun CurrencyEnum.shortcut(context: Context): String = context.getString(
    when (this) {
        CurrencyEnum.Zloty -> R.string.pln_currency
        CurrencyEnum.Dolar -> R.string.dol_currency
        CurrencyEnum.Euro -> R.string.eur_currency
    }
)

fun CurrencyEnum.extendedName(context: Context): String = context.getString(
    when (this) {
        CurrencyEnum.Zloty -> R.string.pln_currency_extended
        CurrencyEnum.Dolar -> R.string.dol_currency_extended
        CurrencyEnum.Euro -> R.string.eur_currency_extended
    }
)

fun CurrencyEnum.formatForText(context: Context, text: String) = when (this) {
    CurrencyEnum.Dolar,
    CurrencyEnum.Euro -> "${this.shortcut(context)} $text"

    CurrencyEnum.Zloty -> "$text ${this.shortcut(context)}"
}

fun Long.toDate(): String = DateUtils.formatDateFromLong(this)
fun Long.toTime(): String = DateUtils.formatTimeFromLong(this)

fun Double.toTwoDigits(): String = DecimalFormat("#.##").format(this)

fun Car.name() = "${this.brand} ${this.model}"

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) =
    (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)