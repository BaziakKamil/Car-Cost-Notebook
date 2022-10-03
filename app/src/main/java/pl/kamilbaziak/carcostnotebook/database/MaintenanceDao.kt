package pl.kamilbaziak.carcostnotebook.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pl.kamilbaziak.carcostnotebook.Constants
import pl.kamilbaziak.carcostnotebook.model.Maintenance

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM ${Constants.MAINTENANCE_TABLE}")
    fun getMaintenanceData(): LiveData<List<Maintenance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMaintenance(maintenance: Maintenance)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMaintenance(maintenance: Maintenance)

    @Delete
    suspend fun deleteMaintenance(maintenance: Maintenance)
}