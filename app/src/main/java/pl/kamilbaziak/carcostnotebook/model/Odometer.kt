package pl.kamilbaziak.carcostnotebook.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pl.kamilbaziak.carcostnotebook.Constants.ODOMETER_TABLE
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum

@Entity(tableName = ODOMETER_TABLE)
@Parcelize
data class Odometer(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val carId: Long,
    val input: Double,
    val unit: UnitEnum,
    val created: Long,
    val canBeDeleted: Boolean,
    val description: String? = null
) : Parcelable
