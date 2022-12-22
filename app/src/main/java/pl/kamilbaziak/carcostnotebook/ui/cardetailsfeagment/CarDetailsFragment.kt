package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentCarDetailsBinding
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab.MaintenanceFragment
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab.OdometerFragment
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab.TankFillFragment
import pl.kamilbaziak.carcostnotebook.ui.maintenancedialog.MaintenanceDialog
import pl.kamilbaziak.carcostnotebook.ui.odometerdialog.OdometerDialog
import pl.kamilbaziak.carcostnotebook.ui.tankfilldialog.TankFillDialog

class CarDetailsFragment : Fragment() {

    private val args: CarDetailsFragmentArgs by navArgs()
    private val car by lazy {
        args.car
    }
    private val binding by lazy {
        FragmentCarDetailsBinding.inflate(layoutInflater)
    }
    private val viewModel: CarDetailsViewModel by viewModel {
        parametersOf(car.id)
    }
    private val animFadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = 300 }
    private val animFadeOut = AlphaAnimation(1.0f, 0.0f).apply { duration = 50 }

    private val viewPagerAdapter by lazy {
        ViewPagerAdapter(childFragmentManager, lifecycle).apply {
            addFragment(
                TankFillFragment.newInstance(car.id, car.petrolUnit),
                getString(R.string.petrol),
                R.drawable.ic_petrol
            )
            addFragment(
                OdometerFragment.newInstance(car.id, car.unit),
                getString(R.string.odometer),
                R.drawable.ic_odometer
            )
            addFragment(
                MaintenanceFragment.newInstance(car.id),
                getString(R.string.maintenance),
                R.drawable.ic_maintenance
            )
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

        fabAddContainer.apply {
            fabAddPetrol.setOnClickListener {
                viewPager.setCurrentItem(0, true)
                TankFillDialog.show(childFragmentManager, car.id)
                extendAddFab(false)
            }

            fabAddOdometer.setOnClickListener {
                viewPager.setCurrentItem(1, true)
                OdometerDialog.show(childFragmentManager, car.id)
                extendAddFab(false)
            }

            fabAddMaintenance.setOnClickListener {
                viewPager.setCurrentItem(2, true)
                MaintenanceDialog.show(childFragmentManager, car.id)
                extendAddFab(false)
            }
        }

        viewModel.apply {
            tankFillCount.observe(viewLifecycleOwner) {
                tabLayout.getTabAt(0)!!.orCreateBadge.number = it.size
            }

            odometerCount.observe(viewLifecycleOwner) {
                tabLayout.getTabAt(1)!!.orCreateBadge.number = it.size
            }

            maintenanceCount.observe(viewLifecycleOwner) {
                tabLayout.getTabAt(2)!!.orCreateBadge.number = it.size
            }
        }

        return@run
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
                    textPetrol.startAnimation(animFadeIn)
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
