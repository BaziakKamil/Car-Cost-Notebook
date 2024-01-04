package pl.kamilbaziak.carcostnotebook.ui.carsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import pl.kamilbaziak.carcostnotebook.databinding.FragmentCarListBinding
import pl.kamilbaziak.carcostnotebook.model.name
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialogActions

class CarListFragment : Fragment(), MaterialAlertDialogActions {

    private val viewModel: CarsListViewModel by inject()
    private val binding: FragmentCarListBinding by lazy {
        FragmentCarListBinding.inflate(layoutInflater)
    }
    private val adapter: CarListAdapter by lazy {
        CarListAdapter(
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
    private var showAlertDialog = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.composeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
//                if(showAlertDialog) {
//                    MaterialDialog(
//                        onDismissRequest = { showAlertDialog = false },
//                        onConfirmation = { showAlertDialog = false },
//                        dialogTitle = "Title",
//                        dialogText = "Dialog text",
//                        icon = Icons.Outlined.List
//                    )
//                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        setOptionsMenu()

        recycler.apply {
            adapter = this@CarListFragment.adapter
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
                    CarsListViewModel.MainViewEvent.AddNewCar ->
                        findNavController().navigate(
                            CarListFragmentDirections.actionMainViewFragmentToAddNewCarFragment(
                                getString(R.string.add_new_car)
                            )
                        )

                    is CarsListViewModel.MainViewEvent.NavigateToCarDetails -> findNavController().navigate(
                        CarListFragmentDirections.actionMainViewFragmentToCarDetailsFragment(
                            event.car,
                            event.odometer,
                            "${event.car.brand} ${event.car.model} ${event.car.year}"
                        )
                    )

                    is CarsListViewModel.MainViewEvent.ShowCarEditDialogScreen ->
                        findNavController().navigate(
                            CarListFragmentDirections.actionMainViewFragmentToAddNewCarFragment(
                                event.car.name(),
                                event.car
                            )
                        )

                    is CarsListViewModel.MainViewEvent.ShowCarDeleteDialogMessage ->
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            getString(R.string.delete_dialog_title, event.car.name()),
                            getString(R.string.cannot_be_undone),
                            getString(R.string.delete)
                        )

                    is CarsListViewModel.MainViewEvent.ShowUndoDeleteCarMessage ->
                        TextUtils.showSnackbarWithAction(
                            requireView(),
                            getString(R.string.car_deleted),
                            getString(R.string.undo)
                        ) {
                            viewModel.onUndoDeleteCar()
                        }

                    is CarsListViewModel.MainViewEvent.ShowDeleteErrorMessage ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.error_during_delete_process)
                        )

                    is CarsListViewModel.MainViewEvent.ShowBackupImportDialog ->
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            "Import car data backup files",
                            "Choose which backup file to import",
                            list = event.files
                        )

                    is CarsListViewModel.MainViewEvent.ShowErrorDialogMessage -> {
                        MaterialAlertDialog.show(
                            childFragmentManager,
                            getString(R.string.something_went_wrong),
                            event.message,
                            getString(R.string.accept)
                        )
                    }

                    is CarsListViewModel.MainViewEvent.ShowSnackbarMessage ->
                        TextUtils.showSnackbar(
                            requireView(),
                            event.message
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
                        showAlertDialog = true
                        return true
                    }

                    R.id.menu_import_from_csv -> {
                        viewModel.importDatabase()
                        return true
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

    override fun getItemListItemTitle(title: String) {
        viewModel.startImportFromJsonToDatabase(title)
    }
}