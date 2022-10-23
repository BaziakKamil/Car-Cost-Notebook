package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
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
    private val animFadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = 300 }
    private val animFadeOut = AlphaAnimation(1.0f, 0.0f).apply { duration = 80 }

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

        extendAddFab(false)

        fabAddContainer.fabAdd.setOnClickListener {
            extendAddFab(!fabAddContainer.fabAdd.isExtended)
        }

        textCarName.text = "${car.brand} ${car.model}"
    }

    private fun extendAddFab(extend: Boolean) = binding.run {
        fabAddContainer.apply {
            textMaintenance.isVisible = extend
            textAddOdometer.isVisible = extend
            when (extend) {
                true -> {
                    textMaintenance.startAnimation(animFadeIn)
                    textAddOdometer.startAnimation(animFadeIn)
                    fabAdd.extend()
                    fabAddMaintenance.show()
                    fabAddOdometer.show()
                }
                else -> {
                    textMaintenance.startAnimation(animFadeOut)
                    textAddOdometer.startAnimation(animFadeOut)
                    fabAdd.shrink()
                    fabAddMaintenance.hide()
                    fabAddOdometer.hide()
                }
            }
        }
    }
}
