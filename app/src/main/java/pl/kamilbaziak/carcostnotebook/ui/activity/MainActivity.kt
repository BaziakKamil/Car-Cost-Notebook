package pl.kamilbaziak.carcostnotebook.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.kamilbaziak.carcostnotebook.databinding.ActivityMainBinding
import pl.kamilbaziak.carcostnotebook.ui.addnewcarfragment.AddNewCarFragment
import pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.CarDetailsFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModel<MainViewModel>()
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (supportFragmentManager.backStackEntryCount == 0) {
                finish()
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        lifecycleScope.launch {
            viewModel.mainViewModelEvents.collect { event ->
                supportFragmentManager.commit {
                    replace(
                        binding.fragmentContainer.id,
                        when (event) {
                            is MainActivityEvent.OpenAddNewCar -> AddNewCarFragment.newInstance(
                                event.car,
                                event.title
                            )

                            is MainActivityEvent.OpenCarDetails -> CarDetailsFragment.newInstance(
                                event.car,
                                event.odometer,
                                event.title
                            )
                        },
                        event.tag
                    )
                    addToBackStack(null)
                }
            }
        }
    }
}
