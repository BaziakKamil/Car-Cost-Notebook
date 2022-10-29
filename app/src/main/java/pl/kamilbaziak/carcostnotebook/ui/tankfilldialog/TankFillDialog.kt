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
import pl.kamilbaziak.carcostnotebook.enums.EngineEnum
import pl.kamilbaziak.carcostnotebook.enums.PetrolEnum
import pl.kamilbaziak.carcostnotebook.toDate

class TankFillDialog : BottomSheetDialogFragment() {

    private val binding by lazy {
        DialogTankfillBinding.inflate(layoutInflater)
    }
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
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

        viewModel.pickedDate.observe(viewLifecycleOwner) {
            textInputCalendar.editText?.setText(it.toDate())
        }

        buttonDone.setOnClickListener {
            validate(
                textInputPetrolType.editText?.text.toString(),
                textInputPetrolQuantity.editText?.text.toString(),
                textInputPetrolPrice.editText?.text.toString(),
                textInputOdometer.editText?.text.toString(),
                textInputComputerReading.editText?.text.toString(),
                textInputPetrolStation.editText?.text.toString()
            )
        }
        buttonCancel.setOnClickListener { dismiss() }
        imageClose.setOnClickListener { dismiss() }
    }

    //todo validation with snackbar messages
    private fun validate(
        petrolType: String?,
        petrolQuantity: String?,
        petrolPrice: String?,
        odometer: String?,
        computerReading: String?,
        petrolStation: String?
    ) {
        if (petrolQuantity.isNullOrEmpty()) {
            return
        }
        if (petrolPrice.isNullOrEmpty()) {
            return
        }
        viewModel.addTankFill(
            carId!!,
            getPetrolEnumFromName(petrolType),
            petrolQuantity.toInt(),
            petrolPrice.toDouble(),
            odometer?.toDouble(),
            computerReading?.toDouble(),
            petrolStation
        )
        dismiss()
    }

    companion object {
        const val TAG = "TankFillDialog.TAG"
        const val DATE_PICKER_TAG = "TankFillDialog.DATE_PICKER_TAG"
        const val CAR_ID_EXTRA = "TankFillDialog.CAR_ID_EXTRA"

        fun show(
            fragmentManager: FragmentManager,
            carId: Long
        ) = TankFillDialog().apply {
            arguments = bundleOf(CAR_ID_EXTRA to carId)
        }.show(fragmentManager, TAG)
    }
}
