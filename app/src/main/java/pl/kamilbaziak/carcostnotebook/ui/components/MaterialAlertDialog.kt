package pl.kamilbaziak.carcostnotebook.ui.components

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pl.kamilbaziak.carcostnotebook.databinding.AlertDialogMaterialBinding
import pl.kamilbaziak.carcostnotebook.ui.SimpleSingleItemAdapter

class MaterialAlertDialog: BottomSheetDialogFragment() {

    private lateinit var binding: AlertDialogMaterialBinding
    private val title by lazy { arguments?.getString(EXTRA_TITLE) }
    private val description by lazy { arguments?.getString(EXTRA_DESCRIPTION) }
    private val confirmButtonText by lazy { arguments?.getString(EXTRA_CONFIRM_BUTTON_TEXT) }
    private val list by lazy { arguments?.getStringArrayList(EXTRA_LIST) }
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
        this@MaterialAlertDialog.confirmButtonText?.let {
            confirmButton.apply {
                isVisible = true
                text = it
            }
        } ?: run { confirmButton.isVisible = false }
        list?.let { backupList ->
            recycler.apply {
                isVisible = backupList.isNotEmpty()
                adapter = SimpleSingleItemAdapter {
                    materialAlertDialogActions?.getItemListItemTitle(it)
                    this@MaterialAlertDialog.dismiss()
                }.also { it.submitList(backupList) }
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(false)

            }
        } ?: run { recycler.isVisible = false }

        closeButton.setOnClickListener {
            materialAlertDialogActions?.onNegativeButtonClicked()
            dismiss()
        }

        confirmButton.setOnClickListener {
            materialAlertDialogActions?.onPositiveButtonClicked()
            dismiss()
        }
    }

    companion object Contract {

        const val TAG = "MaterialAlertDialog"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_CONFIRM_BUTTON_TEXT = "EXTRA_CONFIRM_BUTTON_TEXT"
        const val EXTRA_LIST = "EXTRA_LIST"

        fun show(
            fragmentManager: FragmentManager,
            title: String,
            description: String,
            confirmButtonText: String? = null,
            list: ArrayList<String>? = null
        ) = MaterialAlertDialog().apply {
            arguments = bundleOf(
                EXTRA_TITLE to title,
                EXTRA_DESCRIPTION to description,
                EXTRA_CONFIRM_BUTTON_TEXT to confirmButtonText,
                EXTRA_LIST to list
            )
        }.show(fragmentManager, TAG)
    }
}