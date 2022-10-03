package pl.kamilbaziak.carcostnotebook.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.kamilbaziak.carcostnotebook.Constants
import pl.kamilbaziak.carcostnotebook.model.TankFill

@Dao
interface TankFillDao {

    @Query("SELECT * FROM ${Constants.TANK_FILL_TABLE}")
    fun getTankFillData(): LiveData<List<TankFill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTankFill(tankFill: TankFill)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTankFill(tankFill: TankFill)

    @Delete
    suspend fun deleteTankFill(tankFill: TankFill)
}