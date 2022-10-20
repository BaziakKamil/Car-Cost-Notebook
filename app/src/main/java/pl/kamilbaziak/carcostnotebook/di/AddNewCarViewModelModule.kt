package pl.kamilbaziak.carcostnotebook.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment.AddNewCarViewModel

val addNewCarViewModelModule = module {
    viewModel { AddNewCarViewModel(get(), get()) }
}
