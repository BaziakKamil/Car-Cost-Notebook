package pl.kamilbaziak.carcostnotebook.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.ui.activity.MainViewModel
import pl.kamilbaziak.carcostnotebook.ui.newcar.AddNewCarViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetails.CarDetailsViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetails.details.DetailsViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetails.maintenancetab.MaintenanceViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetails.odometertab.OdometerViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetails.petroltab.TankFillViewModel
import pl.kamilbaziak.carcostnotebook.ui.maintenancedialog.MaintenanceDialogViewModel
import pl.kamilbaziak.carcostnotebook.ui.cars.CarsListViewModel
import pl.kamilbaziak.carcostnotebook.ui.odometerdialog.OdometerDialogViewModel
import pl.kamilbaziak.carcostnotebook.ui.tankfilldialog.TankFillDialogViewModel

val viewModelsModule = module {

    viewModel { MainViewModel() }
    viewModel { CarsListViewModel(androidApplication(), get(), get(), get(), get()) }
    viewModel { AddNewCarViewModel(get(), get()) }
    viewModel { parameters -> CarDetailsViewModel(get(), get(), get(), parameters[0]) }
    viewModel { parameters -> TankFillViewModel(get(), get(), parameters[0]) }
    viewModel { parameters -> OdometerViewModel(get(), parameters[0]) }
    viewModel { parameters -> MaintenanceViewModel(get(), get(), parameters[0]) }
    viewModel { parameters ->
        TankFillDialogViewModel(get(), get(), get(), parameters[0])
    }
    viewModel { parameters -> OdometerDialogViewModel(get(), get(), parameters[0]) }
    viewModel { MaintenanceDialogViewModel(get(), get(), get()) }
    viewModel { parameters -> DetailsViewModel(get(), get(), get(), get(), parameters[0]) }
}
