package pl.kamilbaziak.carcostnotebook.ui.tankfilldialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.EnumUtils
import pl.kamilbaziak.carcostnotebook.EnumUtils.getPetrolEnumFromName
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.DialogTankfillBinding
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.model.TankFill
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits

class TankFillDialog : BottomSheetDialogFragment() {

    private val binding by lazy {
        DialogTankfillBinding.inflate(layoutInflater)
    }
    private val carId by lazy { arguments?.getLong(EXTRA_CAR_ID) }
    private val tankFill by lazy { arguments?.get(EXTRA_TANK_FILL) as? TankFill }
    private val viewModel: TankFillDialogViewModel by inject()
    private val dateDialog = MaterialDatePicker.Builder.datePicker()
        .setTitleText(R.string.choose_date)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        tankFill?.let {
            textInputPetrolType.editText?.setText(it.petrolEnum.name)
            textInputPetrolQuantity.editText?.setText(it.quantity.toTwoDigits())
            textInputPetrolPrice.editText?.setText(it.petrolPrice?.toTwoDigits())
            viewModel.getOdometerForTankFill(it.odometerId)
            textInputComputerReading.editText?.setText(it.computerReading?.toTwoDigits())
            textInputPetrolStation.editText?.setText(it.petrolStation)
            textInputDistanceFromLastFill.editText?.setText(it.distanceFromLastTankFill?.toTwoDigits())
            viewModel.changePickedDate(it.created)
        }

        EnumUtils.setEnumValuesToMaterialSpinner(
            textInputPetrolType.editText as MaterialAutoCompleteTextView,
            buildList {
                PetrolEnum.values().map {
                    add(it.name)
                }
            }
        )

        dateDialog.addOnPositiveButtonClickListener {
            viewModel.changePickedDate(it)
        }

        textInputCalendar.editText?.setOnClickListener {
            dateDialog.show(childFragmentManager, DATE_PICKER_TAG)
        }

        viewModel.apply {
            pickedDate.observe(viewLifecycleOwner) {
                textInputCalendar.editText?.setText(it.toDate())
            }
            odometerForTankFill.observe(viewLifecycleOwner) {
                if (it != null) {
                    textInputOdometer.editText?.setText(it.input.toTwoDigits())
                }
            }
        }

        buttonDone.setOnClickListener {
            validate(
                textInputPetrolType.editText?.text.toString(),
                textInputPetrolQuantity.editText?.text.toString(),
                textInputPetrolPrice.editText?.text.toString(),
                textInputDistanceFromLastFill.editText?.text.toString(),
                textInputOdometer.editText?.text.toString(),
                textInputComputerReading.editText?.text.toString(),
                textInputPetrolStation.editText?.text.toString()
            )
        }
        buttonCancel.setOnClickListener { dismiss() }
        imageClose.setOnClickListener { dismiss() }
    }

    private fun validate(
        petrolType: String?,
        petrolQuantity: String?,
        petrolPrice: String?,
        distanceFromLastFill: String?,
        odometer: String?,
        computerReading: String?,
        petrolStation: String?
    ) {
        resetInputErrors()
        if (!validateData(petrolQuantity, petrolPrice, odometer, petrolStation)) return

        viewModel.addTankFill(
            carId!!,
            getPetrolEnumFromName(petrolType),
            petrolQuantity!!.toDouble(),
            petrolPrice!!.toDouble(),
            if (distanceFromLastFill.isNullOrEmpty()) 0.0 else distanceFromLastFill.toDouble(),
            odometer!!.toDouble(),
            if (computerReading.isNullOrEmpty()) 0.0 else computerReading.toDouble(),
            petrolStation!!,
            tankFill
        )
        dismiss()
    }

    private fun validateData(
        petrolQuantity: String?,
        petrolPrice: String?,
        odometer: String?,
        petrolStation: String?
    ): Boolean {
        binding.apply {
            if (petrolQuantity.isNullOrEmpty()) {
                textInputPetrolQuantity.error = "Enter petrol quantity"
            } else if (petrolPrice.isNullOrEmpty()) {
                textInputPetrolPrice.error = "Enter petrol price"
            } else if (odometer.isNullOrEmpty()) {
                textInputOdometer.error = "Enter odometer value"
            } else if (petrolStation.isNullOrEmpty()) {
                textInputPetrolStation.error = "Enter petrol station name"
            } else {
                return true
            }
            return false
        }
    }

    private fun resetInputErrors() = binding.apply {
        textInputPetrolQuantity.error = null
        textInputPetrolPrice.error = null
        textInputOdometer.error = null
        textInputPetrolStation.error = null
    }

    companion object {
        const val TAG = "TankFillDialog.TAG"
        const val DATE_PICKER_TAG = "TankFillDialog.DATE_PICKER_TAG"
        const val EXTRA_CAR_ID = "TankFillDialog.EXTRA_CAR_ID"
        const val EXTRA_TANK_FILL = "TankFillDialog.EXTRA_TANK_FILL"

        fun show(
            fragmentManager: FragmentManager,
            carId: Long?,
            tankFill: TankFill? = null
        ) = TankFillDialog().apply {
            arguments = bundleOf(
                EXTRA_CAR_ID to carId,
                EXTRA_TANK_FILL to tankFill
            )
        }.show(fragmentManager, TAG)
    }
}