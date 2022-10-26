package pl.kamilbaziak.carcostnotebook.ui.odometerdialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.kamilbaziak.carcostnotebook.databinding.DialogOdometerBinding

class OdometerDialog : DialogFragment() {

    private val binding by lazy {
        DialogOdometerBinding.inflate(layoutInflater)
    }
    private val odometerDialogViewModel: OdometerDialogViewModel by viewModel()
    private val carId by lazy {
        arguments?.getLong(CAR_ID_EXTRA)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        textInputOdometer.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { buttonDone.isActivated = !s.isNullOrEmpty() }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        textInputCalendar.editText?.setOnClickListener {
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
                .show(childFragmentManager, "TTT")
        }

        buttonDone.setOnClickListener { dismiss() }
        buttonCancel.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "OdometerDialog.TAG"
        const val CAR_ID_EXTRA = "CAR_ID_EXTRA"

        fun show(
            fragmentManager: FragmentManager,
            carId: Long
        ) {
            OdometerDialog().also {
                it.arguments = bundleOf(CAR_ID_EXTRA to carId)
            }.show(
                fragmentManager,
                TAG
            )
        }
    }
}
