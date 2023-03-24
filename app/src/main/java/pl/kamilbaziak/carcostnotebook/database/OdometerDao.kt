package pl.kamilbaziak.carcostnotebook.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.kamilbaziak.carcostnotebook.Constants
import pl.kamilbaziak.carcostnotebook.model.Odometer

@Dao
interface OdometerDao {

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId ORDER BY created DESC")
    fun getOdometerLiveData(carId: Long): LiveData<List<Odometer>>

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId ORDER BY created DESC")
    suspend fun getAllOdometerDataForCar(carId: Long): List<Odometer>

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId ORDER BY created ASC LIMIT 1")
    suspend fun getFirstCarOdometer(carId: Long): Odometer?

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId ORDER BY created ASC LIMIT 1")
    suspend fun getFirstCarOdometer(carId: Long): Odometer?

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId ORDER BY created DESC LIMIT 1")
    suspend fun getLastCarOdometer(carId: Long): Odometer?

    @Query("SELECT * FROM ${Constants.ODOMETER_TABLE} WHERE id = :odometerId")
    suspend fun getOdometerById(odometerId: Long): Odometer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOdometer(odometer: Odometer): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOdometer(odometer: Odometer)

    @Delete
    suspend fun deleteOdometer(odometer: Odometer)

    @Query("DELETE FROM ${Constants.ODOMETER_TABLE} WHERE id = :odometerId")
    suspend fun deleteOdometerById(odometerId: Long)

    @Query("DELETE FROM ${Constants.ODOMETER_TABLE} WHERE carId = :carId")
    suspend fun deleteOdometer(carId: Long)
}
