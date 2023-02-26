package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.EnumUtils.getUnitTypeFromName
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.FragmentOdometerBinding
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog
import pl.kamilbaziak.carcostnotebook.ui.odometerdialog.OdometerDialog

class OdometerFragment : Fragment(), MaterialAlertDialog.MaterialAlertDialogActions {

    private val binding by lazy {
        FragmentOdometerBinding.inflate(layoutInflater)
    }
    private val viewModel: OdometerViewModel by viewModel {
        parametersOf(carId)
    }
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
    private val unit by lazy { arguments?.getString(UNIT_EXTRA) }
    private val adapter by lazy {
        OdometerAdapter(
            { viewModel.onEditOdometer(it) },
            { viewModel.onDeleteOdometer(it) },
            getUnitTypeFromName(unit)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        recycler.apply {
            adapter = this@OdometerFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.odometerEvent.collect { event ->
                when (event) {
                    OdometerViewModel.OdometerEvent.ShowOdometerSavedConfirmationMessage ->
                        TextUtils.showSnackbar(requireView(), getString(R.string.odometer_added_correctly))
                    is OdometerViewModel.OdometerEvent.ShowUndoDeleteOdometerMessage ->
                        TextUtils.showSnackbarWithAction(
                            requireView(),
                            getString(R.string.odometer_deleted),
                            getString(R.string.undo)
                        ) {
                            viewModel.onUndoDeleteOdometer(event.odometer)
                        }
                    is OdometerViewModel.OdometerEvent.ShowDeleteErrorSnackbar ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.error_during_delete_process)
                        )
                    is OdometerViewModel.OdometerEvent.ShowOdometerDeleteDialogMessage ->
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            getString(R.string.delete_dialog_odometer_title),
                            getString(R.string.delete_dialog_message),
                            getString(R.string.delete)
                        )
                    is OdometerViewModel.OdometerEvent.ShowOdometerEditDialogScreen ->
                        OdometerDialog.show(childFragmentManager, carId, event.odometer)
                }
            }
        }

        viewModel.odometerAll.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.sortedByDescending { it.created })
        }
    }

    companion object {
        const val CAR_ID_EXTRA = "OdometerFragment.CAR_ID_EXTRA"
        const val UNIT_EXTRA = "OdometerFragment.UNIT_EXTRA"

        fun newInstance(carId: Long, unit: UnitEnum) = OdometerFragment().apply {
            arguments = bundleOf(
                CAR_ID_EXTRA to carId,
                UNIT_EXTRA to unit.name
            )
        }
    }

    override fun onConfirm() {
        viewModel.deleteOdometer()
    }
}
