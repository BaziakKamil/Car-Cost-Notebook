package pl.kamilbaziak.carcostnotebook.ui.maintenancedialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.DialogMaintenanceBinding
import pl.kamilbaziak.carcostnotebook.toDate

class MaintenanceDialog : BottomSheetDialogFragment() {

    private val binding by lazy {
        DialogMaintenanceBinding.inflate(layoutInflater)
    }
    private val carId by lazy {
        arguments?.getLong(CAR_ID_EXTRA)
    }
    private val viewModel: MaintenanceDialogViewModel by inject()
    private val dateDialog = MaterialDatePicker.Builder.datePicker()
        .setTitleText(R.string.choose_date)
        .build()
    private val dueDateDialog = MaterialDatePicker.Builder.datePicker()
        .setTitleText(R.string.choose_due_date)
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

        dueDateDialog.addOnPositiveButtonClickListener {
            viewModel.changePickedDueDate(it)
        }

        textInputCalendar.editText?.setOnClickListener {
            dateDialog.show(childFragmentManager, DATE_PICKER_TAG)
        }

        viewModel.apply {
            pickedDate.observe(viewLifecycleOwner) {
                textInputCalendar.editText?.setText(it.toDate())
            }

            pickedDueDate.observe(viewLifecycleOwner) {
                if (it != null) {
                    textInputDueDate.editText?.setText(it.toDate())
                }
            }
        }

        buttonDone.setOnClickListener {
            validate(
                textInputName.editText?.text.toString(),
                textInputPrice.editText?.text.toString(),
                textInputOdometer.editText?.text.toString(),
                textInputDescription.editText?.text.toString()
            )
        }
        buttonCancel.setOnClickListener { dismiss() }
        imageClose.setOnClickListener { dismiss() }
    }

    // todo validation with snackbar messages
    private fun validate(
        name: String?,
        price: String?,
        odometer: String?,
        description: String?
    ) {
        if (name.isNullOrEmpty()) {
            return
        }
        viewModel.addMaintenance(
            carId!!,
            name,
            price?.toDouble(),
            odometer?.toDouble(),
            description
        )
        dismiss()
    }

    companion object {
        const val TAG = "MaintenanceDialog.TAG"
        const val DATE_PICKER_TAG = "MaintenanceDialog.DATE_PICKER_TAG"
        const val CAR_ID_EXTRA = "MaintenanceDialog.CAR_ID_EXTRA"

        fun show(
            fragmentManager: FragmentManager,
            carId: Long
        ) = MaintenanceDialog().apply {
            arguments = bundleOf(CAR_ID_EXTRA to carId)
        }.show(fragmentManager, TAG)
    }
}
