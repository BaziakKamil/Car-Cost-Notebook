package pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentAddNewCarBinding
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum

class AddNewCarFragment : Fragment(R.layout.fragment_add_new_car) {

    private val binding by lazy {
        FragmentAddNewCarBinding.inflate(layoutInflater)
    }
    private val viewModel: AddNewCarViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KeyboardVisibilityEvent.setEventListener(
            requireActivity()
        ) { isKeyboardShown ->
            when (isKeyboardShown) {
                true -> binding.fabAddCar.hide()
                else -> binding.fabAddCar.show()
            }
        }
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

        (textInputEngineType.editText as MaterialAutoCompleteTextView).setSimpleItems(buildList {
            EngineEnum.values().map {
                add(it.name)
            }
        }.toTypedArray())

        (textInputUnit.editText as MaterialAutoCompleteTextView).setSimpleItems(buildList {
            UnitEnum.values().map {
                add(it.name)
            }
        }.toTypedArray())

        fabAddCar.setOnClickListener {
            saveCar()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addNewCarEvent.collect { event ->
                when (event) {
                    AddNewCarViewModel.AddNewCarEvent.NavigateBack ->
                        findNavController().popBackStack()
                }
            }
        }

        return@run
    }

    private fun saveCar() {
        if (!validateData()) return

        binding.apply {
            viewModel.addCar(
                textInputCarBrand.editText?.text.toString(),
                textInputCarModel.editText?.text.toString(),
                textInputCarYear.editText?.text.toString().toInt(),
                textInputCarLicencePlate.editText?.text.toString(),
                getEngineTypeFromName(textInputEngineType.editText?.text.toString()),
                textInputCarOdometer.editText?.text.toString().toDouble(),
                getUnitTypeFromName(textInputUnit.editText?.text.toString()),
                textInputDescription.editText?.text.toString()
            )
        }
    }

    private fun validateData(): Boolean {
        resetTextInputErrors()
        binding.apply {
            if (textInputCarBrand.editText?.text.toString().isEmpty()) {
                textInputCarBrand.error = getString(R.string.insert_car_brand)
            } else if (textInputCarModel.editText?.text.toString().isEmpty()) {
                textInputCarModel.error = getString(R.string.insert_car_model)
            } else if (textInputCarOdometer.editText?.text.toString().isEmpty()) {
                textInputCarOdometer.error = getString(R.string.insert_car_mileage)
            } else if (textInputEngineType.editText?.text.toString().isEmpty()) {
                textInputEngineType.error = getString(R.string.choose_engine_type)
            } else if (textInputUnit.editText?.text.toString().isEmpty()) {
                textInputUnit.error = getString(R.string.choose_odometer_unit)
            } else {
                return true
            }
            return false
        }
    }

    private fun resetTextInputErrors() = binding.run {
        textInputCarBrand.error = null
        textInputCarModel.error = null
        textInputCarOdometer.error = null
        textInputEngineType.error = null
        textInputUnit.error = null
    }

    private fun getEngineTypeFromName(name: String): EngineEnum = EngineEnum.values().find {
        it.name == name
    }!!

    private fun getUnitTypeFromName(name: String): UnitEnum = UnitEnum.values().find {
        it.name == name
    }!!

    private fun showSnackbar(message: String) =
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .show()
}