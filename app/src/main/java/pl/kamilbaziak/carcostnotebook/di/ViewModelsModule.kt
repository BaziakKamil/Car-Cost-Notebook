package pl.kamilbaziak.carcostnotebook.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment.AddNewCarViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab.OdometerViewModel
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.CarDetailsViewModel
import pl.kamilbaziak.carcostnotebook.ui.mainviewfragment.MainViewViewModel
import pl.kamilbaziak.carcostnotebook.ui.odometerdialog.OdometerDialogViewModel

val viewModelsModule = module {

    viewModel { MainViewViewModel(get(), get()) }
    viewModel { AddNewCarViewModel(get(), get()) }
    viewModel { CarDetailsViewModel(get()) }
    viewModel { OdometerDialogViewModel(get()) }
    viewModel { OdometerViewModel(get()) }
}
