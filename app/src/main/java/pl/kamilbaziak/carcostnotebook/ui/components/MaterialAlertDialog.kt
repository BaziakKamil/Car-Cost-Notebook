package pl.kamilbaziak.carcostnotebook.ui.components

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pl.kamilbaziak.carcostnotebook.databinding.AlertDialogMaterialBinding

class MaterialAlertDialog: BottomSheetDialogFragment() {

    private lateinit var binding: AlertDialogMaterialBinding
    private val title by lazy { arguments?.getString(EXTRA_TITLE) }
    private val description by lazy { arguments?.getString(EXTRA_DESCRIPTION) }
    private val confirmButtonText by lazy { arguments?.getString(EXTRA_CONFIRM_BUTTON_TEXT) }
    private val materialAlertDialogActions: MaterialAlertDialogActions? by lazy {
        (context as? MaterialAlertDialogActions) ?: parentFragment as? MaterialAlertDialogActions
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AlertDialogMaterialBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        (view.parent as View).apply {
            backgroundTintMode = PorterDuff.Mode.CLEAR
            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            setBackgroundColor(Color.TRANSPARENT)
        }
        title.text = this@MaterialAlertDialog.title
        description.text = this@MaterialAlertDialog.description
        confirmButton.text = this@MaterialAlertDialog.confirmButtonText

        closeButton.setOnClickListener {
            dismiss()
        }

        confirmButton.setOnClickListener {
            materialAlertDialogActions?.onConfirm()
            dismiss()
        }
    }

    interface MaterialAlertDialogActions {
        fun onConfirm()
    }

    companion object Contract {

        const val TAG = "MaterialAlertDialog"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_CONFIRM_BUTTON_TEXT = "EXTRA_CONFIRM_BUTTON_TEXT"

        fun show(
            fragmentManager: FragmentManager,
            title: String,
            description: String,
            confirmButtonText: String
        ) = MaterialAlertDialog().apply {
            arguments = bundleOf(
                EXTRA_TITLE to title,
                EXTRA_DESCRIPTION to description,
                EXTRA_CONFIRM_BUTTON_TEXT to confirmButtonText
            )
        }.show(fragmentManager, TAG)
    }
}