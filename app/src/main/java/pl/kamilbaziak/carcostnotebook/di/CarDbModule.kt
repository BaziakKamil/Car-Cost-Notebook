package pl.kamilbaziak.carcostnotebook.di

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.database.CarDatabase

val carDatabase = module {
    fun provideDatabase(application: Application): CarDatabase = Room.databaseBuilder(
        application,
        CarDatabase::class.java,
        "car_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    fun provideCarDao(database: CarDatabase) = database.carDao

    fun provideMaintenanceDao(database: CarDatabase) = database.maintenanceDao

    fun provideOdometerDao(database: CarDatabase) = database.odometerDao

    fun provideTankFillDao(database: CarDatabase) = database.tankFillDao

    single { provideDatabase(androidApplication()) }
    single { provideCarDao(get()) }
    single { provideMaintenanceDao(get()) }
    single { provideOdometerDao(get()) }
    single { provideTankFillDao(get()) }
}
