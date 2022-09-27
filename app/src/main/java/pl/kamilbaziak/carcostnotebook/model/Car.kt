package pl.kamilbaziak.carcostnotebook.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pl.kamilbaziak.carcostnotebook.Constants.CAR_TABLE
import pl.kamilbaziak.carcostnotebook.enums.EngineType
import pl.kamilbaziak.carcostnotebook.enums.UnitType

@Entity(tableName = CAR_TABLE)
@Parcelize
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val brand: String,
    val model: String,
    val engineType: EngineType,
    val odometer: Double,
    val unit: UnitType,
    val description: String
): Parcelable