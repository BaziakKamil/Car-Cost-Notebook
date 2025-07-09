package pl.kamilbaziak.carcostnotebook.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.kamilbaziak.carcostnotebook.Constants
import pl.kamilbaziak.carcostnotebook.model.Maintenance

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM ${Constants.MAINTENANCE_TABLE} WHERE carId = :carId ORDER BY created DESC")
    fun getMaintenanceLiveData(carId: Long): LiveData<List<Maintenance>>

    @Query("SELECT * FROM ${Constants.MAINTENANCE_TABLE} WHERE carId = :carId ORDER BY created DESC")
    suspend fun getMaintenanceData(carId: Long): List<Maintenance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMaintenance(maintenance: Maintenance)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMaintenance(maintenance: Maintenance)

    @Delete
    suspend fun deleteMaintenance(maintenance: Maintenance)

    @Query("DELETE FROM ${Constants.MAINTENANCE_TABLE} WHERE carId = :carId")
    suspend fun deleteMaintenance(carId: Long)

    @Query("SELECT * FROM ${Constants.MAINTENANCE_TABLE} ORDER BY created DESC")
    suspend fun getAllMaintenance(): List<Maintenance>
}
