package pl.kamilbaziak.carcostnotebook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.kamilbaziak.carcostnotebook.model.Car

@Database(entities = [Car::class], version = 1, exportSchema = false)
abstract class CarDatabase: RoomDatabase() {
    abstract val carDao: CarDao
}