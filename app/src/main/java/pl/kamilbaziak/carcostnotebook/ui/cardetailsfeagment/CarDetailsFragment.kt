package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.FragmentCarDetailsBinding
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.details.DetailsFragment
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
            addFragment(DetailsFragment.newInstance(car.id))
            addFragment(TankFillFragment.newInstance(car.id, car.petrolUnit))
            addFragment(MaintenanceFragment.newInstance(car.id))
            addFragment(OdometerFragment.newInstance(car.id, car.unit))
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

        viewPager.apply {
            isSaveEnabled = false
            adapter = viewPagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    extendAddFab(false)
                    bottomNavigation.selectedItemId = when (position) {
                        1 -> R.id.menu_petrol
                        2 -> R.id.menu_maintenance
                        3 -> R.id.menu_odometer
                        else -> R.id.menu_car_details
                    }
                }
            })
        }

        bottomNavigation.setOnItemSelectedListener {
            extendAddFab(false)
            when (it.itemId) {
                R.id.menu_car_details -> viewPager.currentItem = 0
                R.id.menu_petrol -> viewPager.currentItem = 1
                R.id.menu_maintenance -> viewPager.currentItem = 2
                R.id.menu_odometer -> viewPager.currentItem = 3
                R.id.menu_add -> extendAddFab(!fabAddContainer.fabAddPetrol.isVisible)
            }
            true
        }

        extendAddFab(false)

        fabAddContainer.fabAdd.setOnClickListener {
            extendAddFab(!fabAddContainer.fabAdd.isExtended)
        }

        fabAddContainer.apply {
            fabAddPetrol.setOnClickListener {
                viewPager.setCurrentItem(0, true)
                bottomNavigation.selectedItemId = R.id.menu_petrol
                TankFillDialog.show(childFragmentManager, car.id)
                extendAddFab(false)
            }

            fabAddMaintenance.setOnClickListener {
                viewPager.setCurrentItem(1, true)
                bottomNavigation.selectedItemId = R.id.menu_maintenance
                MaintenanceDialog.show(childFragmentManager, car.id)
                extendAddFab(false)
            }

            fabAddOdometer.setOnClickListener {
                viewPager.setCurrentItem(2, true)
                bottomNavigation.selectedItemId = R.id.menu_odometer
                OdometerDialog.show(childFragmentManager, car.id)
                extendAddFab(false)
            }
        }

        viewModel.apply {
            tankFillCount.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    bottomNavigation.getOrCreateBadge(R.id.menu_petrol).apply {
                        isVisible = true
                        number = it.size
                    }
                }
            }

            maintenanceCount.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    bottomNavigation.getOrCreateBadge(R.id.menu_maintenance).apply {
                        isVisible = true
                        number = it.size
                    }
                }
            }

            odometerCount.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    bottomNavigation.getOrCreateBadge(R.id.menu_odometer).apply {
                        isVisible = true
                        number = it.size
                    }
                }
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
//                    fabAdd.extend()
                    fabAddMaintenance.show()
                    fabAddOdometer.show()
                    fabAddPetrol.show()
                }
                else -> {
                    textMaintenance.startAnimation(animFadeOut)
                    textAddOdometer.startAnimation(animFadeOut)
                    textPetrol.startAnimation(animFadeOut)
//                    fabAdd.shrink()
                    fabAddMaintenance.hide()
                    fabAddOdometer.hide()
                    fabAddPetrol.hide()
                }
            }
        }
    }
}
