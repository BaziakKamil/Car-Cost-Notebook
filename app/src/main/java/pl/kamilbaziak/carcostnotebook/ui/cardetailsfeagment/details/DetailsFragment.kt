package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.FragmentDetailsBinding
import pl.kamilbaziak.carcostnotebook.name
import pl.kamilbaziak.carcostnotebook.shortcut
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.CarDetailsFragmentDirections
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialogActions

class DetailsFragment : Fragment(), MaterialAlertDialogActions {

    private val binding by lazy {
        FragmentDetailsBinding.inflate(layoutInflater)
    }
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
    private val viewModel: DetailsViewModel by viewModel {
        parametersOf(carId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        setOptionsMenu()

        viewModel.apply {
            currentCarData.observe(viewLifecycleOwner) {c ->
                c?.let { car ->
                    textInputCarBrand.editText?.setText(car.brand)
                    textInputCarModel.editText?.setText(car.model)
                    textInputCarYear.editText?.setText(car.year.toString())
                    textInputCarLicencePlate.editText?.setText(car.licensePlate)
                    textInputDescription.editText?.setText(car.description.ifEmpty { getString(R.string.not_added) })
                    textInputCarDateWhenBought.editText?.setText(
                        car.dateWhenBought?.toDate() ?: getString(R.string.not_added)
                    )
                    textInputCarPriceWhenBought.editText?.setText(
                        car.priceWhenBought?.let {
                           "${car.priceWhenBought?.toTwoDigits()} ${car.currency}"
                        } ?: getString(R.string.not_added)
                    )
                    allOdometerData.observe(viewLifecycleOwner) { list ->
                        if(list.isNotEmpty()) {
                            textInputTotalDistanceMade.editText?.setText(
                                "${(list.maxOf { it.input } - list.minOf { it.input }).toTwoDigits()} ${car.unit.shortcut()}"
                            )
                        }
                    }

                    allTankFillData.observe(viewLifecycleOwner) { list ->
                        val totalFuelConsumption =  list.sumOf { it.quantity }.toTwoDigits()
                        val totalFuelCost = list.sumOf { it.petrolPrice?.times(it.quantity) ?: 0.0 }.toTwoDigits()

                        textInputTotalFuelConsuption.editText?.setText("$totalFuelConsumption ${car.petrolUnit.shortcut()}")
                        textInputTotalFuelPaid.editText?.setText("$totalFuelCost ${car.currency}")
                    }

                    allMaintenance.observe(viewLifecycleOwner) { list ->
                        val totalMaintenanceCost =  list.sumOf { it.price ?: 0.0 }.toTwoDigits()
                        textInputTotalMaintenancePaid.editText?.setText("$totalMaintenanceCost ${car.currency}")
                    }
                } ?: run{
                    TextUtils.showSnackbar(requireView(), getString(R.string.something_went_wrong))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.detailsViewEvent.collect { event ->
                when (event) {
                    is DetailsViewModel.DetailsViewEvent.CarDeleted ->
                        findNavController().navigateUp()
                    is DetailsViewModel.DetailsViewEvent.ErrorDuringDeleteProcedure ->
                        TextUtils.showSnackbar(requireView(), getString(R.string.error_during_delete_process))
                    is DetailsViewModel.DetailsViewEvent.ShowCarDeleteDialogMessage ->
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            getString(
                                R.string.delete_dialog_title,
                                event.car?.name() ?: getString(R.string.not_added)
                            ),
                            getString(R.string.cannot_be_undone),
                            getString(R.string.delete)
                        )
                    is DetailsViewModel.DetailsViewEvent.EditCar ->
                        findNavController().navigate(
                            CarDetailsFragmentDirections.actionCarDetailsFragmentToAddNewCarFragment(
                                event.car!!.name(),
                                event.car
                            )
                        )
                }
            }
        }

        return@run
    }

    private fun setOptionsMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.more_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.delete -> {
                        viewModel.onCarDelete()
                        true
                    }
                    R.id.edit -> {
                        viewModel.onCarEdit()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onPositiveButtonClicked() {
        viewModel.deleteCar()
    }

    override fun onNegativeButtonClicked() {}

    override fun getItemListItemTitle(title: String) {}

    companion object Contract {
        const val CAR_ID_EXTRA = "DetailsFragment.CAR_ID_EXTRA"

        fun newInstance(carId: Long) = DetailsFragment().apply {
            arguments = bundleOf(
                CAR_ID_EXTRA to carId
            )
        }
    }
}