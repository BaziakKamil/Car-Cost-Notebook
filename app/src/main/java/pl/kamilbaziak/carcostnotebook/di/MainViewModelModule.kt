package pl.kamilbaziak.carcostnotebook.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.kamilbaziak.carcostnotebook.ui.mainviewfragment.MainViewViewModel

val mainViewModelModule = module {
    viewModel {
        MainViewViewModel(get(), get())
    }
}
