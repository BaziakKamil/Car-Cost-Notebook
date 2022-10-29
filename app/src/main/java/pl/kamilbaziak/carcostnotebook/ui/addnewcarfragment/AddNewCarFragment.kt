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
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.EnumUtils.getEngineTypeFromName
import pl.kamilbaziak.carcostnotebook.EnumUtils.getPetrolUnitFromName
import pl.kamilbaziak.carcostnotebook.EnumUtils.getUnitTypeFromName
import pl.kamilbaziak.carcostnotebook.EnumUtils.setEnumValuesToMaterialSpinner
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentAddNewCarBinding
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Car

class AddNewCarFragment : Fragment() {

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

        setEnumValuesToMaterialSpinner(
            textInputEngineType.editText as MaterialAutoCompleteTextView,
            buildList {
                EngineEnum.values().map {
                    add(it.name)
                }
            }
        )

        setEnumValuesToMaterialSpinner(
            textInputPetrolUnit.editText as MaterialAutoCompleteTextView,
            buildList {
                PetrolUnitEnum.values().map {
                    add(it.name)
                }
            }
        )

        setEnumValuesToMaterialSpinner(
            textInputUnit.editText as MaterialAutoCompleteTextView,
            buildList {
                UnitEnum.values().map {
                    add(it.name)
                }
            }
        )

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
                Car(
                    0,
                    textInputCarBrand.editText?.text.toString(),
                    textInputCarModel.editText?.text.toString(),
                    textInputCarYear.editText?.text.toString().toInt(),
                    textInputCarLicencePlate.editText?.text.toString(),
                    getEngineTypeFromName(textInputEngineType.editText?.text.toString()),
                    getPetrolUnitFromName(textInputPetrolUnit.editText?.text.toString()),
                    getUnitTypeFromName(textInputUnit.editText?.text.toString()),
                    textInputDescription.editText?.text.toString()
                ),
                textInputCarOdometer.editText?.text.toString().toDouble()
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
                textInputEngineType.error = getString(R.string.choose_petrol_type)
            } else if (textInputPetrolUnit.editText?.text.toString().isEmpty()) {
                textInputPetrolUnit.error = getString(R.string.choose_petrol_unit)
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
        textInputPetrolUnit.error = null
        textInputUnit.error = null
    }

    private fun showSnackbar(message: String) =
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .show()
}
