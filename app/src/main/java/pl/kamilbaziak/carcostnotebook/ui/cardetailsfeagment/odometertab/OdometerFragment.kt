package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.kamilbaziak.carcostnotebook.EnumUtils.getUnitTypeFromName
import pl.kamilbaziak.carcostnotebook.databinding.FragmentOdometerBinding
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer

class OdometerFragment : Fragment() {

    private val binding by lazy {
        FragmentOdometerBinding.inflate(layoutInflater)
    }
    private val viewModel: OdometerViewModel by viewModel()
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
    private val unit by lazy { arguments?.getString(UNIT_EXTRA) }
    private val adapter by lazy {
        OdometerAdapter(
            { adapterClick(it) },
            getUnitTypeFromName(unit)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
        super.onViewCreated(view, savedInstanceState)

        recycler.apply {
            adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        viewModel.odometerAll.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun adapterClick(odometer: Odometer) {
        Toast.makeText(requireContext(), "${odometer.input}", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val CAR_ID_EXTRA = "OdometerFragment.CAR_ID_EXTRA"
        const val UNIT_EXTRA = "OdometerFragment.UNIT_EXTRA"

        fun newInstance(carId: Long, unit: UnitEnum) = OdometerFragment().apply {
            arguments = bundleOf(
                CAR_ID_EXTRA to carId,
                UNIT_EXTRA to unit.name
            )
        }
    }
}
