package pl.kamilbaziak.carcostnotebook.ui.carsfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.TextUtils
import pl.kamilbaziak.carcostnotebook.databinding.FragmentMainViewBinding
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.name

class CarsFragment : Fragment() {

    private val viewModel: CarsViewModel by inject()
    private val binding: FragmentMainViewBinding by lazy {
        FragmentMainViewBinding.inflate(layoutInflater)
    }
    private val adapter: CarAdapter by lazy {
        CarAdapter(
            { viewModel.onCarClick(it) },
            { viewModel.onCarEdit(it) },
            { viewModel.onCarDelete(it) }
        )
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

        recycler.apply {
            adapter = this@CarsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.cars.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            noCarView.root.isVisible = it.isEmpty()
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
                    is CarsViewModel.MainViewEvent.ShowCarDeleteDialogMessage -> {
                        showDeleteDialog(event.car)
                    }
                    is CarsViewModel.MainViewEvent.ShowDeleteErrorSnackbar ->
                        TextUtils.showSnackbar(
                            requireView(),
                            getString(R.string.error_during_delete_process)
                        )
                }
            }
        }

        return@run
    }

    @SuppressLint("StringFormatInvalid")
    private fun showDeleteDialog(car: Car) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_car, car.name()))
            .setMessage(getString(R.string.cannot_be_undone))
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.accept) { dialog, _ ->
                viewModel.deleteCar()
                dialog.dismiss()
            }
            .show()
    }
}
