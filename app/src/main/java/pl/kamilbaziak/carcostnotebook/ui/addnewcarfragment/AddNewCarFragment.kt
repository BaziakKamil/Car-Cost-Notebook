package pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentAddNewCarBinding
import pl.kamilbaziak.carcostnotebook.enums.EngineType
import pl.kamilbaziak.carcostnotebook.enums.UnitType
import pl.kamilbaziak.carcostnotebook.model.Car

class AddNewCarFragment : Fragment(R.layout.fragment_add_new_car) {

    private val binding by lazy {
        FragmentAddNewCarBinding.inflate(layoutInflater)
    }
    private val viewModel: AddNewCarViewModel by inject()
    private val engineAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            EngineType.values().toMutableList()
        )
    }
    private val unitAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            UnitType.values().toMutableList()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        spinnerEngineType.adapter = engineAdapter
        spinnerUnit.adapter = unitAdapter

        fabAddCar.setOnClickListener {
            saveCar()
        }
    }

    private fun saveCar() {
        if (!validateData()) return

        binding.apply {
            viewModel.addCar(
                Car(
                    1,
                    editTextCarName.text.toString(),
                    editTextCarBrand.text.toString(),
                    editTextCarModel.text.toString(),
                    spinnerEngineType.selectedItem as EngineType,
                    editTextOdometer.text.toString().toDouble(),
                    spinnerUnit.selectedItem as UnitType,
                    editTextDescription.text.toString()
                )
            )
        }
    }

    private fun validateData(): Boolean {
        binding.apply {
            val message: String = if (editTextCarName.text.isNullOrEmpty()) {
                getString(R.string.insert_car_name)
            } else if (editTextOdometer.text.isNullOrEmpty()) {
                getString(R.string.insert_car_mileage)
            } else {
                return true
            }
            showSnackbar(message)
            return false
        }
    }

    private fun showSnackbar(message: String) =
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .show()
}