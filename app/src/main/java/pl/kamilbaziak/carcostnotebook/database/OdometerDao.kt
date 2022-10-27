package pl.kamilbaziak.carcostnotebook.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.kamilbaziak.carcostnotebook.Constants
import pl.kamilbaziak.carcostnotebook.model.Odometer

@Dao
interface OdometerDao {

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE}")
    fun getOdometerData(): LiveData<List<Odometer>>

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId ORDER BY created DESC")
    fun getAllOdometerForCar(carId: Long): LiveData<List<Odometer>>

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId ORDER BY created DESC LIMIT 1")
    fun getLastCarOdometer(carId: Long): LiveData<Odometer?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOdometer(odometer: Odometer)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOdometer(odometer: Odometer)

    @Delete
    suspend fun deleteOdometer(odometer: Odometer)
}
