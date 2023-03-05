package pl.kamilbaziak.carcostnotebook.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pl.kamilbaziak.carcostnotebook.Constants

object Migrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
           database.execSQL(
               "ALTER TABLE ${Constants.ODOMETER_TABLE} ADD COLUMN description TEXT"
           )
        }
    }
}