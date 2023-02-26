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
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits

class MaintenanceDialog : BottomSheetDialogFragment() {

    private val binding by lazy {
        DialogMaintenanceBinding.inflate(layoutInflater)
    }
    private val carId by lazy {
        arguments?.getLong(EXTRA_CAR_ID)
    }
    private val maintenance by lazy {
        arguments?.get(EXTRA_MAINTENANCE) as? Maintenance
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

        maintenance?.let {
            textTitle.text = getString(R.string.edit_maintenance_details)
            viewModel.changePickedDate(it.created)
            viewModel.changePickedDueDate(it.dueDate)
            textInputName.editText?.setText(it.name)
            textInputPrice.editText?.setText(it.price?.toTwoDigits())
            textInputDescription.editText?.setText(it.description)
            it.odometerId?.let { odometer -> viewModel.getOdometerForMaintenance(odometer) }
        }

        dateDialog.addOnPositiveButtonClickListener {
            viewModel.changePickedDate(it)
        }

        dueDateDialog.addOnPositiveButtonClickListener {
            viewModel.changePickedDueDate(it)
        }

        textInputCalendar.editText?.setOnClickListener {
            dateDialog.show(childFragmentManager, DATE_PICKER_TAG)
        }

        textInputDueDate.editText?.setOnClickListener {
            dueDateDialog.show(childFragmentManager, DATE_PICKER_TAG)
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

            odometerForMaintenance.observe(viewLifecycleOwner) {
                if (it != null) {
                    textInputOdometer.editText?.setText(it.input.toTwoDigits())
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
            description,
            maintenance
        )
        dismiss()
    }

    companion object {
        const val TAG = "MaintenanceDialog.TAG"
        const val DATE_PICKER_TAG = "MaintenanceDialog.DATE_PICKER_TAG"
        const val DUE_DATE_PICKER_TAG = "MaintenanceDialog.DUE_DATE_PICKER_TAG"
        const val EXTRA_CAR_ID = "MaintenanceDialog.EXTRA_CAR_ID"
        const val EXTRA_MAINTENANCE = "MaintenanceDialog.EXTRA_MAINTENANCE"

        fun show(
            fragmentManager: FragmentManager,
            carId: Long?,
            maintenance: Maintenance? = null
        ) = MaintenanceDialog().apply {
            arguments = bundleOf(
                EXTRA_CAR_ID to carId,
                EXTRA_MAINTENANCE to maintenance
            )
        }.show(fragmentManager, TAG)
    }
}
