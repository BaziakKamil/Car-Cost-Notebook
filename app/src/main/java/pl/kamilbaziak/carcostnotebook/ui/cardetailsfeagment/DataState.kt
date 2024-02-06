package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

sealed interface DataState {

    object Progress: DataState
    object NotFound: DataState
    data class Found(val list: List<*>) : DataState
}