package pl.kamilbaziak.carcostnotebook.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import pl.kamilbaziak.carcostnotebook.Constants.CAR_TABLE
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum

@Entity(tableName = CAR_TABLE)
@Parcelize
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val brand: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val engineEnum: EngineEnum,
    val petrolUnit: PetrolUnitEnum,
    val unit: UnitEnum,
    val description: String,
    val priceWhenBought: Double? = null,
    val dateWhenBought: Long? = null,
    val currency: String
) : Parcelable

fun Car.name() = "${this.brand} ${this.model}"
