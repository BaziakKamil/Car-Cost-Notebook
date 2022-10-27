package pl.kamilbaziak.carcostnotebook.ui.odometerdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.DialogOdometerBinding
import pl.kamilbaziak.carcostnotebook.hasLetters
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.toDate
import java.util.Date

class OdometerDialog : BottomSheetDialogFragment() {

    private val binding by lazy {
        DialogOdometerBinding.inflate(layoutInflater)
    }
    private val carId by lazy {
        arguments?.getLong(CAR_ID_EXTRA)
    }
    private val viewModel: OdometerDialogViewModel by inject()
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
            validate(textInputOdometer.editText?.text.toString())
        }
        buttonCancel.setOnClickListener { dismiss() }
        imageClose.setOnClickListener { dismiss() }
    }

    private fun validate(odometer: String?) {
        if (odometer.isNullOrEmpty()) {
            return
        }
        if (odometer.hasLetters()) {
            return
        }
        viewModel.addOdometer(
            Odometer(
                0,
                carId!!,
                odometer.toDouble(),
                viewModel.pickedDate.value ?: Date().time
            )
        )
        dismiss()
    }

    companion object {
        const val TAG = "OdometerDialog.TAG"
        const val DATE_PICKER_TAG = "OdometerDialog.DATE_PICKER_TAG"
        const val CAR_ID_EXTRA = "OdometerDialog.CAR_ID_EXTRA"

        fun show(
            fragmentManager: FragmentManager,
            carId: Long
        ) = OdometerDialog().apply {
            arguments = bundleOf(CAR_ID_EXTRA to carId)
        }.show(fragmentManager, TAG)
    }
}
