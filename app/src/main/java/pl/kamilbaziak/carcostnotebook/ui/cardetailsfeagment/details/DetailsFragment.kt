package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentDetailsBinding
import pl.kamilbaziak.carcostnotebook.model.name

class DetailsFragment : Fragment() {

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

        viewModel.apply {
            currentCarData.observe(viewLifecycleOwner) {
                it?.let { car ->

                    //set text here
                } ?: run{
                    noCarViewGroup.isVisible = true
                    textCarName.text = getString(R.string.error_getting_car_data)
                }
            }
        }
        return@run
    }

    companion object Contract {
        const val CAR_ID_EXTRA = "DetailsFragment.CAR_ID_EXTRA"

        fun newInstance(carId: Long) = DetailsFragment().apply {
            arguments = bundleOf(
                CAR_ID_EXTRA to carId
            )
        }
    }
}