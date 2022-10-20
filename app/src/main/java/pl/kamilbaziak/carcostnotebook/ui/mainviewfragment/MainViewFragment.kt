package pl.kamilbaziak.carcostnotebook.ui.mainviewfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentMainViewBinding
import pl.kamilbaziak.carcostnotebook.model.Car

class MainViewFragment : Fragment(R.layout.fragment_main_view), CarAdapter.OnItemClickListener {

    private val viewModel: MainViewViewModel by inject()
    private val binding: FragmentMainViewBinding by lazy {
        FragmentMainViewBinding.inflate(layoutInflater)
    }
    private val adapter: CarAdapter by lazy {
        CarAdapter(this)
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
            adapter = this@MainViewFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.cars.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        fabAddCar.setOnClickListener {
            viewModel.onAddNewCarClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.mainViewEvent.collect { event ->
                when (event) {
                    MainViewViewModel.MainViewEvent.AddnewCar ->
                        findNavController().navigate(
                            MainViewFragmentDirections.actionMainViewFragmentToAddNewCarFragment()
                        )
                    is MainViewViewModel.MainViewEvent.NavigateToCarDetails -> findNavController().navigate(
                        MainViewFragmentDirections.actionMainViewFragmentToCarDetailsFragment(
                            event.car,
                            event.odometer,
                            "${event.car.brand} ${event.car.model}"
                        )
                    )
                }
            }
        }

        return@run
    }

    override fun onItemClicked(car: Car) {
        viewModel.onCarClick(car)
    }
}
