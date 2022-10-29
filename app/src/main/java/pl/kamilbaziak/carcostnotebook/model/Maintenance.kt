package pl.kamilbaziak.carcostnotebook.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pl.kamilbaziak.carcostnotebook.Constants.MAINTENANCE_TABLE
import pl.kamilbaziak.carcostnotebook.empty
import java.text.DateFormat

@Entity(tableName = MAINTENANCE_TABLE)
@Parcelize
data class Maintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val carId: Long,
    val name: String,
    val price: Double?,
    val odometerId: Long?,
    val created: Long,
    val dueDate: Long?,
    val notifyWhenDue: Boolean?,
    val description: String?
) : Parcelable
