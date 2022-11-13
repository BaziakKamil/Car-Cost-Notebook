package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.FragmentMaintenanceBinding
import pl.kamilbaziak.carcostnotebook.model.Maintenance

class MaintenanceFragment : Fragment() {

    private val binding by lazy {
        FragmentMaintenanceBinding.inflate(layoutInflater)
    }
    private val viewModel: MaintenanceViewModel by viewModel {
        parametersOf(carId)
    }
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
    private val adapter by lazy {
        MaintenanceAdapter { adapterClick(it) }
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
                        TextUtils.showSnackbar(requireView(), "Maintenance added correctly")
                    is MaintenanceViewModel.MaintenanceEvent.ShowUndoDeleteMaintenanceMessage ->
                        TextUtils.showSnackbarWithAction(
                            requireView(),
                            "Maintenance ${event.maintenance.name} deleted",
                            "UNDO"
                        ) {
                            viewModel.onUndoDeleteMaintenance(event.maintenance)
                        }
                }
            }
        }

        viewModel.maintenanceAll.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun adapterClick(maintenance: Maintenance) {
        Toast.makeText(requireContext(), maintenance.name, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val CAR_ID_EXTRA = "MaintenanceFragment.CAR_ID_EXTRA"

        fun newInstance(carId: Long) = MaintenanceFragment().apply {
            arguments = bundleOf(CAR_ID_EXTRA to carId)
        }
    }
}
