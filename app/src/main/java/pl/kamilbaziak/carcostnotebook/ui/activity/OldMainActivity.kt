package pl.kamilbaziak.carcostnotebook.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.kamilbaziak.carcostnotebook.databinding.ActivityMainBinding
import pl.kamilbaziak.carcostnotebook.ui.newcar.AddNewCarFragment
import pl.kamilbaziak.carcostnotebook.ui.cardetails.CarDetailsFragment

class OldMainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.mainViewModelEvents.collect { event ->
                when (event) {
                    is MainActivityEvent.OpenAddNewCar -> supportFragmentManager.commit {
                        add(
                            binding.fragmentContainer.id,
                            AddNewCarFragment.newInstance(
                                event.car
                            ),
                            event.tag
                        )
                        addToBackStack(null)
                    }

                    is MainActivityEvent.OpenCarDetails -> supportFragmentManager.commit {
                        add(
                            binding.fragmentContainer.id,
                            CarDetailsFragment.newInstance(
                                event.car,
                                event.odometer,
                                event.title
                            ),
                            event.tag
                        )
                        addToBackStack(null)
                    }

                    else -> Unit
                }
            }
        }
    }
}
