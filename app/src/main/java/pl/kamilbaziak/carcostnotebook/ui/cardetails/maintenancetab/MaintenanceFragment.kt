package pl.kamilbaziak.carcostnotebook.ui.cardetails.maintenancetab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.FragmentMaintenanceBinding
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.ui.cardetails.DataState
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialogActions
import pl.kamilbaziak.carcostnotebook.ui.maintenancedialog.MaintenanceDialog

class MaintenanceFragment : Fragment(), MaterialAlertDialogActions {

    private val binding by lazy {
        FragmentMaintenanceBinding.inflate(layoutInflater)
    }
    private val viewModel: MaintenanceViewModel by viewModel {
        parametersOf(carId)
    }
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
    private val adapter by lazy {
        MaintenanceAdapter(
            { viewModel.onEditMaintenance(it) },
            { viewModel.onDeleteMaintenance(it) }
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
            adapter = this@MaintenanceFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.maintenanceEvent.collect { event ->
                when (event) {
                    MaintenanceViewModel.MaintenanceEvent.ShowMaintenanceSavedConfirmationMessage ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.maintenance_added_correctly)
                        )

                    is MaintenanceViewModel.MaintenanceEvent.ShowCarEditDialogScreen ->
                        MaintenanceDialog.show(
                            childFragmentManager,
                            carId,
                            event.maintenance
                        )

                    is MaintenanceViewModel.MaintenanceEvent.ShowMaintenanceDeleteDialogMessage ->
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            getString(R.string.delete_dialog_title, event.maintenance.name),
                            getString(R.string.delete_dialog_message),
                            getString(R.string.delete)
                        )

                    is MaintenanceViewModel.MaintenanceEvent.ShowUndoDeleteMaintenanceMessage -> {
                        TextUtils.showSnackbarWithAction(
                            requireView(),
                            getString(R.string.maintenace_deleted, event.maintenance.name),
                            getString(R.string.undo)
                        ) {
                            viewModel.onUndoDeleteMaintenance(
                                event.maintenance,
                                event.pairedOdometer
                            )
                        }
                    }

                    is MaintenanceViewModel.MaintenanceEvent.ShowDeleteErrorSnackbar ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.error_during_delete_process)
                        )
                }
            }
        }

        viewModel.maintenanceData.observe(viewLifecycleOwner) {
            viewModel.prepareMaintenanceData(it)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            progress.isVisible = state is DataState.Progress
            layoutNotFound.isVisible = state is DataState.NotFound
            recycler.isVisible = state is DataState.Found

            if (state is DataState.Found) {
                adapter.submitList(state.list as List<Maintenance>)
            }
        }
    }

    override fun onPositiveButtonClicked() {
        viewModel.deleteMaintenance()
    }

    override fun onNegativeButtonClicked() {}

    override fun getItemListItemTitle(title: String) {}

    companion object {
        const val CAR_ID_EXTRA = "MaintenanceFragment.CAR_ID_EXTRA"

        fun newInstance(carId: Long) = MaintenanceFragment().apply {
            arguments = bundleOf(CAR_ID_EXTRA to carId)
        }
    }
}
