package pl.kamilbaziak.carcostnotebook

import android.annotation.SuppressLint
import pl.kamilbaziak.carcostnotebook.Constants.DATE_FORMAT
import pl.kamilbaziak.carcostnotebook.Constants.TIME_FORMAT
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    @SuppressLint("SimpleDateFormat")
    fun formatDateFromLong(long: Long): String =
        SimpleDateFormat(DATE_FORMAT).format(Date(long))

    @SuppressLint("SimpleDateFormat")
    fun formatTimeFromLong(long: Long): String =
        SimpleDateFormat(TIME_FORMAT).format(Date(long))
}
