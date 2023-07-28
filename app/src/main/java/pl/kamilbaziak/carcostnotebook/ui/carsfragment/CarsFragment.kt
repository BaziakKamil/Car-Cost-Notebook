package pl.kamilbaziak.carcostnotebook.ui.carsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.DialogProgressBinding
import pl.kamilbaziak.carcostnotebook.databinding.FragmentCarBinding
import pl.kamilbaziak.carcostnotebook.model.name
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog

class CarsFragment : Fragment(), MaterialAlertDialog.MaterialAlertDialogActions {

    private val viewModel: CarsViewModel by inject()
    private val binding: FragmentCarBinding by lazy {
        FragmentCarBinding.inflate(layoutInflater)
    }
    private val adapter: CarAdapter by lazy {
        CarAdapter(
            { viewModel.onCarClick(it) },
            { viewModel.onCarEdit(it) },
            { viewModel.onCarDelete(it) }
        )
    }
    private val progressDialog by lazy {
        AlertDialog.Builder(requireActivity()).apply {
            setView(
                DialogProgressBinding.inflate(layoutInflater).apply {
                    textProgressDialog.text = "test if setting text is working aas intended"
                }.root
            )
                .setCancelable(false)
        }.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        setOptionsMenu()

        recycler.apply {
            adapter = this@CarsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.apply {
            cars.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    viewModel.setupCarMappedData(it)
                }
            }

            carsMapped.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                noCarView.root.isVisible = it.isEmpty()
            }
        }

        fabAddCar.setOnClickListener {
            viewModel.onAddNewCarClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.mainViewEvent.collect { event ->
                when (event) {
                    CarsViewModel.MainViewEvent.AddNewCar ->
                        findNavController().navigate(
                            CarsFragmentDirections.actionMainViewFragmentToAddNewCarFragment(
                                getString(R.string.add_new_car)
                            )
                        )

                    is CarsViewModel.MainViewEvent.NavigateToCarDetails -> findNavController().navigate(
                        CarsFragmentDirections.actionMainViewFragmentToCarDetailsFragment(
                            event.car,
                            event.odometer,
                            "${event.car.brand} ${event.car.model} ${event.car.year}"
                        )
                    )

                    is CarsViewModel.MainViewEvent.ShowCarEditDialogScreen ->
                        findNavController().navigate(
                            CarsFragmentDirections.actionMainViewFragmentToAddNewCarFragment(
                                event.car.name(),
                                event.car
                            )
                        )

                    is CarsViewModel.MainViewEvent.ShowCarDeleteDialogMessage ->
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            getString(R.string.delete_dialog_title, event.car.name()),
                            getString(R.string.cannot_be_undone),
                            getString(R.string.delete)
                        )

                    is CarsViewModel.MainViewEvent.ShowUndoDeleteCarMessage ->
                        TextUtils.showSnackbarWithAction(
                            requireView(),
                            getString(R.string.car_deleted),
                            getString(R.string.undo)
                        ) {
                            viewModel.onUndoDeleteCar()
                        }

                    is CarsViewModel.MainViewEvent.ShowDeleteErrorMessage ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.error_during_delete_process)
                        )
                }
            }
        }

        return@run
    }

    private fun setOptionsMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_export_database -> {
                        viewModel.exportDatabase()
                        TextUtils.showSnackbar(binding.root, "Export database")
                        return true
                    }

                    R.id.menu_import_from_csv -> {
                        TextUtils.showSnackbar(binding.root, "Import to database")
                        return true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onConfirm() {
        viewModel.deleteCar()
    }
}