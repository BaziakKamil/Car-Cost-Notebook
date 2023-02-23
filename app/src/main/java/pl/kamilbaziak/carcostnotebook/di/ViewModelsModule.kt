package pl.kamilbaziak.carcostnotebook.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment.AddNewCarViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.CarDetailsViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab.MaintenanceViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab.OdometerViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab.TankFillViewModel
import pl.kamilbaziak.carcostnotebook.ui.maintenancedialog.MaintenanceDialogViewModel
import pl.kamilbaziak.carcostnotebook.ui.carsfragment.CarsViewModel
import pl.kamilbaziak.carcostnotebook.ui.odometerdialog.OdometerDialogViewModel
import pl.kamilbaziak.carcostnotebook.ui.tankfilldialog.TankFillDialogViewModel

val viewModelsModule = module {

    viewModel { CarsViewModel(get(), get(), get(), get()) }
    viewModel { AddNewCarViewModel(get(), get()) }
    viewModel { parameters -> CarDetailsViewModel(get(), get(), get(), parameters[0]) }
    viewModel { parameters -> TankFillViewModel(get(), get(), parameters[0]) }
    viewModel { parameters -> OdometerViewModel(get(), parameters[0]) }
    viewModel { parameters -> MaintenanceViewModel(get(), get(), parameters[0]) }
    viewModel { TankFillDialogViewModel(get(), get(), get()) }
    viewModel { parameters -> OdometerDialogViewModel(get(), get(), parameters[0]) }
    viewModel { MaintenanceDialogViewModel(get(), get(), get()) }
}
