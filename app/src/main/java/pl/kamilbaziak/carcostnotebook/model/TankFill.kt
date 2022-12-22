package pl.kamilbaziak.carcostnotebook.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pl.kamilbaziak.carcostnotebook.Constants.TANK_FILL_TABLE
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum

@Entity(tableName = TANK_FILL_TABLE)
@Parcelize
data class TankFill(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val carId: Long,
    val petrolEnum: PetrolEnum,
    val quantity: Double,
    val petrolPrice: Double?,
    val distanceFromLastTankFill: Double?,
    val odometerId: Long,
    val computerReading: Double?,
    val petrolStation: String,
    val created: Long
) : Parcelable
