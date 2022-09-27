package pl.kamilbaziak.carcostnotebook.di

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.database.CarDatabase
import pl.kamilbaziak.carcostnotebook.model.Car

val carDatabase = module {
    fun provideDatabase(application: Application): CarDatabase = Room.databaseBuilder(
        application,
        CarDatabase::class.java,
        "car_database"
    )
        .fallbackToDestructiveMigration()
        .build()

    fun provideDao(database: CarDatabase) =
        database.carDao

    single { provideDatabase(androidApplication()) }
    single { provideDao(get()) }
}