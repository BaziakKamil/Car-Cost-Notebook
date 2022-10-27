package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentCarDetailsBinding
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab.MaintenanceFragment
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab.OdometerFragment
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab.PetrolFragment
import pl.kamilbaziak.carcostnotebook.ui.odometerdialog.OdometerDialog

class CarDetailsFragment : Fragment() {

    private val args: CarDetailsFragmentArgs by navArgs()
    private val car by lazy {
        args.car
    }
    private val binding by lazy {
        FragmentCarDetailsBinding.inflate(layoutInflater)
    }
    private val animFadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = 300 }
    private val animFadeOut = AlphaAnimation(1.0f, 0.0f).apply { duration = 80 }

    private val viewPagerAdapter by lazy {
        ViewPagerAdapter(childFragmentManager, lifecycle).apply {
            addFragment(PetrolFragment(), getString(R.string.petrol), R.drawable.ic_petrol)
            addFragment(
                OdometerFragment.newInstance(car.id, car.unit),
                getString(R.string.odometer),
                R.drawable.ic_odometer
            )
            addFragment(MaintenanceFragment(), getString(R.string.maintenance), R.drawable.ic_maintenance)
        }
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

        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = viewPagerAdapter.getPageTitle(position)
            tab.setIcon(viewPagerAdapter.getTabDrawable(position))
            viewPager.setCurrentItem(tab.position, true)
        }.attach()

        extendAddFab(false)

        fabAddContainer.fabAdd.setOnClickListener {
            extendAddFab(!fabAddContainer.fabAdd.isExtended)
        }

        fabAddContainer.fabAddOdometer.setOnClickListener {
            Toast.makeText(requireContext(), "asasa", Toast.LENGTH_LONG).show()
            OdometerDialog.show(childFragmentManager, car.id)
            extendAddFab(false)
        }

        textCarName.text = "${car.brand} ${car.model}"
    }

    private fun extendAddFab(extend: Boolean) = binding.run {
        fabAddContainer.apply {
            textMaintenance.isVisible = extend
            textAddOdometer.isVisible = extend
            textPetrol.isVisible = extend
            when (extend) {
                true -> {
                    textMaintenance.startAnimation(animFadeIn)
                    textAddOdometer.startAnimation(animFadeIn)
                    textAddOdometer.startAnimation(animFadeIn)
                    fabAdd.extend()
                    fabAddMaintenance.show()
                    fabAddOdometer.show()
                    fabAddPetrol.show()
                }
                else -> {
                    textMaintenance.startAnimation(animFadeOut)
                    textAddOdometer.startAnimation(animFadeOut)
                    textPetrol.startAnimation(animFadeOut)
                    fabAdd.shrink()
                    fabAddMaintenance.hide()
                    fabAddOdometer.hide()
                    fabAddPetrol.hide()
                }
            }
        }
    }
}
