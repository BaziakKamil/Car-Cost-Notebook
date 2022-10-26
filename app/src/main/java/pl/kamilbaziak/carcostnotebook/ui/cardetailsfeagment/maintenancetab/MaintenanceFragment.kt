package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.kamilbaziak.carcostnotebook.databinding.FragmentMaintenanceBinding

class MaintenanceFragment : Fragment() {

    private val binding by lazy {
        FragmentMaintenanceBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
