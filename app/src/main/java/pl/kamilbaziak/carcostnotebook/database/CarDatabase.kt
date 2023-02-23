package pl.kamilbaziak.carcostnotebook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill

@Database(
    entities = [
        Car::class,
        Maintenance::class,
        Odometer::class,
        TankFill::class
    ],
    version = 1,
    exportSchema = false
)

abstract class CarDatabase : RoomDatabase() {
    abstract val carDao: CarDao
    abstract val maintenanceDao: MaintenanceDao
    abstract val odometerDao: OdometerDao
    abstract val tankFillDao: TankFillDao
}
