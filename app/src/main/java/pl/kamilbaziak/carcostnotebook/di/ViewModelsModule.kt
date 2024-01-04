package pl.kamilbaziak.carcostnotebook.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment.AddNewCarViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.CarDetailsViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.details.DetailsViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab.MaintenanceViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab.OdometerViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab.TankFillViewModel
import pl.kamilbaziak.carcostnotebook.ui.maintenancedialog.MaintenanceDialogViewModel
import pl.kamilbaziak.carcostnotebook.ui.carsfragment.CarsListViewModel
import pl.kamilbaziak.carcostnotebook.ui.odometerdialog.OdometerDialogViewModel
import pl.kamilbaziak.carcostnotebook.ui.tankfilldialog.TankFillDialogViewModel

val viewModelsModule = module {

    viewModel { CarsListViewModel(androidApplication(), get(), get(), get(), get()) }
    viewModel { AddNewCarViewModel(get(), get()) }
    viewModel { parameters -> CarDetailsViewModel(get(), get(), get(), parameters[0]) }
    viewModel { parameters -> TankFillViewModel(get(), get(), parameters[0]) }
    viewModel { parameters -> OdometerViewModel(get(), parameters[0]) }
    viewModel { parameters -> MaintenanceViewModel(get(), get(), parameters[0]) }
    viewModel { TankFillDialogViewModel(get(), get(), get()) }
    viewModel { parameters -> OdometerDialogViewModel(get(), get(), parameters[0]) }
    viewModel { MaintenanceDialogViewModel(get(), get(), get()) }
    viewModel { parameters -> DetailsViewModel(get(), get(), get(), get(), parameters[0]) }
}
