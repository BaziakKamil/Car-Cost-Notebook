package pl.kamilbaziak.carcostnotebook.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pl.kamilbaziak.carcostnotebook.Constants.ODOMETER_TABLE
import java.text.DateFormat
import java.util.Date

@Entity(tableName = ODOMETER_TABLE)
@Parcelize
data class Odometer(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val carId: Long,
    val input: Double,
    val created: Long
): Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getTimeInstance().format(created)
}