package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab

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
import pl.kamilbaziak.carcostnotebook.EnumUtils.getPetrolUnitFromName
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.FragmentTankFillBinding
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.model.TankFill
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialogActions
import pl.kamilbaziak.carcostnotebook.ui.tankfilldialog.TankFillDialog

class TankFillFragment : Fragment(), MaterialAlertDialogActions {

    private val binding by lazy {
        FragmentTankFillBinding.inflate(layoutInflater)
    }
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
    private val viewModel: TankFillViewModel by viewModel {
        parametersOf(carId)
    }
    private val petrolUnit by lazy { arguments?.getString(PETROL_UNIT_EXTRA) }
    private val adapter by lazy {
        TankFillAdapter(
            { viewModel.onEditTankFill(it) },
            { viewModel.onDeleteTankFill(it) },
            getPetrolUnitFromName(petrolUnit)
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
            adapter = this@TankFillFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tankFillEvent.collect { event ->
                when (event) {
                    TankFillViewModel.TankFillEvent.ShowTankFillSavedConfirmationMessage ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.tank_fill_added_correctly)
                        )
                    is TankFillViewModel.TankFillEvent.ShowUndoDeleteTankFillMessage ->
                        TextUtils.showSnackbarWithAction(
                            requireView(),
                            getString(R.string.tank_fill_deleted),
                            getString(R.string.undo)
                        ) {
                            viewModel.onUndoDeleteTankFill(
                                event.tankFill,
                                event.pairedOdometer
                            )
                        }
                    TankFillViewModel.TankFillEvent.ShowDeleteErrorSnackbar ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.error_during_delete_process)
                        )
                    is TankFillViewModel.TankFillEvent.ShowTankFillDeleteDialogMessage ->
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            getString(R.string.delete_dialog_title),
                            getString(R.string.delete_dialog_message),
                            getString(R.string.delete)
                        )
                    is TankFillViewModel.TankFillEvent.ShowTankFillEditDialogScreen ->
                        TankFillDialog.show(
                            childFragmentManager,
                            carId,
                            event.tankFill
                        )
                }
            }
        }

        viewModel.tankFillAll.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.setupTankFillData(it)
            }
        }

        viewModel.tankFillMapped.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    companion object {
        const val CAR_ID_EXTRA = "TankFillFragment.CAR_ID_EXTRA"
        const val PETROL_UNIT_EXTRA = "TankFillFragment.PETROL_UNIT_EXTRA"

        fun newInstance(
            carId: Long,
            petrolUnit: PetrolUnitEnum
        ) = TankFillFragment().apply {
            arguments = bundleOf(
                CAR_ID_EXTRA to carId,
                PETROL_UNIT_EXTRA to petrolUnit.name
            )
        }
    }

    override fun onPositiveButtonClicked() {
        viewModel.deleteTankFill()
    }

    override fun onNegativeButtonClicked() {}

    override fun getItemListItemTitle(title: String) {}
}
