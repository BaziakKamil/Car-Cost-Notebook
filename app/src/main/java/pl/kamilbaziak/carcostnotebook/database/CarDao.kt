package pl.kamilbaziak.carcostnotebook.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.kamilbaziak.carcostnotebook.Constants.CAR_TABLE
import pl.kamilbaziak.carcostnotebook.model.Car

@Dao
interface CarDao {

    @Query("SELECT * FROM $CAR_TABLE")
    fun getAllCars(): LiveData<List<Car>>

    @Query("SELECT * FROM $CAR_TABLE WHERE id = :carId LIMIT 1")
    fun getCarById(carId: Long): LiveData<Car?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCar(car: Car): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)
}
