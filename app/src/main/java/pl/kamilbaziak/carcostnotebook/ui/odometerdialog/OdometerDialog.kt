package pl.kamilbaziak.carcostnotebook.ui.odometerdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.DialogOdometerBinding
import pl.kamilbaziak.carcostnotebook.hasLetters
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits

class OdometerDialog : BottomSheetDialogFragment() {

    private val binding by lazy {
        DialogOdometerBinding.inflate(layoutInflater)
    }
    private val carId by lazy {
        arguments?.getLong(EXTRA_CAR_ID)
    }
    private val odometer by lazy {
        arguments?.get(EXTRA_ODOMETER) as? Odometer
    }
    private val viewModel: OdometerDialogViewModel by viewModel {
        parametersOf(carId)
    }
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

        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

        odometer?.let {
            textTitle.text = getString(R.string.edit_odometer_reading)
            textInputOdometer.editText?.setText(it.input.toTwoDigits())
            textInputDescription.editText?.setText(it.description)
            viewModel.changePickedDate(it.created)
        }

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
                textInputOdometer.editText?.text.toString(),
                textInputDescription.editText?.text.toString()
            )
        }
        buttonCancel.setOnClickListener { dismiss() }
        imageClose.setOnClickListener { dismiss() }
    }

    private fun validate(
        odometerTxt: String?,
        description: String?
    ) {
        if (odometerTxt.isNullOrEmpty()) {
            return
        }
        if (odometerTxt.hasLetters()) {
            return
        }
        odometer?.let {
            viewModel.updateOdometer(
                odometerTxt.toDouble(),
                description,
                it
            )
        } ?: viewModel.addOdometer(
            odometerTxt.toDouble(),
            description
        )
        dismiss()
    }

    companion object {
        const val TAG = "OdometerDialog.TAG"
        const val DATE_PICKER_TAG = "OdometerDialog.DATE_PICKER_TAG"
        const val EXTRA_CAR_ID = "OdometerDialog.EXTRA_CAR_ID"
        const val EXTRA_ODOMETER = "OdometerDialog.EXTRA_ODOMETER"

        fun show(
            fragmentManager: FragmentManager,
            carId: Long?,
            odometer: Odometer? = null
        ) = OdometerDialog().apply {
            arguments = bundleOf(
                EXTRA_CAR_ID to carId,
                EXTRA_ODOMETER to odometer
            )
        }.show(fragmentManager, TAG)
    }
}
