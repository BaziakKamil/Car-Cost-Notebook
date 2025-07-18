package pl.kamilbaziak.carcostnotebook.ui.carlist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.Constants.BACKUP_DIRECTORY
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.FragmentCarListBinding
import pl.kamilbaziak.carcostnotebook.name
import pl.kamilbaziak.carcostnotebook.ui.activity.MainViewModel
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialog
import pl.kamilbaziak.carcostnotebook.ui.components.MaterialAlertDialogActions
import java.io.File
import androidx.core.net.toUri
import com.google.android.material.shape.MaterialShapeDrawable

class CarListFragment : Fragment(), MaterialAlertDialogActions {

    private val mainViewModel by activityViewModels<MainViewModel>()
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
    private var showAlertDialog = false
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                var title = getString(R.string.something_went_wrong)
                var message = getString(R.string.error_occurred_during_import_process)
                val data = result.data?.data
                if (data != null && data.path != null) {
                    val file = requireContext().contentResolver.openInputStream(data)?.readAllBytes()?.decodeToString()
                    if (file != null) {
                        viewModel.prepareFileForImportToDatabase(file)
                        return@registerForActivityResult
                    } else {
                        title = getString(R.string.wrong_extension_of_file)
                        message = getString(R.string.backup_file_should_end_with_ccn_extension_please_choose_correct_file)
                    }
                }

                MaterialAlertDialog.show(
                    childFragmentManager,
                    title,
                    message
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
        exitTransition = inflater.inflateTransition(R.transition.fade)
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
            adapter = this@CarListFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.apply {
            cars.observe(viewLifecycleOwner) {
                viewModel.setupCarMappedData(it)
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
                        mainViewModel.openAddNewCar(getString(R.string.add_new_car))

                    is CarsListViewModel.MainViewEvent.NavigateToCarDetails ->
                        mainViewModel.openCarDetails(
                            car = event.car,
                            odometer = event.odometer,
                            title = "${event.car.brand} ${event.car.model} ${event.car.year}"
                        )

                    is CarsListViewModel.MainViewEvent.ShowCarEditDialogScreen ->
                        mainViewModel.openAddNewCar(
                            title = event.car.name(),
                            car = event.car
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

                    CarsListViewModel.MainViewEvent.OpenFilePicker ->
                        openFilePickerOnBackupFolder()
                }
            }
        }

        return@run
    }

    private fun openFilePickerOnBackupFolder() {
        filePickerLauncher.launch(
            Intent.createChooser(
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    setDataAndType(
                        arrayOf(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path,
                            BACKUP_DIRECTORY
                        ).joinToString(File.separator).toUri(),
                        "*/*"
                    )
                },
                getString(R.string.choose_backup_file)
            )
        )
    }

    private fun setOptionsMenu() = binding.run {
        toolbar.addMenuProvider(object : MenuProvider {
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

    override fun getItemListItemTitle(title: String) {}
}