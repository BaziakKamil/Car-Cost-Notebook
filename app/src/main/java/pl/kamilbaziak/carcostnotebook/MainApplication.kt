package pl.kamilbaziak.carcostnotebook

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.kamilbaziak.carcostnotebook.di.carDatabase
import pl.kamilbaziak.carcostnotebook.di.viewModelsModule

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(
                carDatabase,
                viewModelsModule
            )
        }
    }
}
