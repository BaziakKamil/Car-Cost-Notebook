package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentCarDetailsBinding

class CarDetailsFragment : Fragment(R.layout.fragment_car_details) {

    private val args: CarDetailsFragmentArgs by navArgs()
    private val car by lazy {
        args.car
    }
    private val binding by lazy {
        FragmentCarDetailsBinding.inflate(layoutInflater)
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

        textCarName.text = "${car.brand} ${car.model}"
    }
}
