package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import pl.kamilbaziak.carcostnotebook.EnumUtils.getPetrolUnitFromName
import pl.kamilbaziak.carcostnotebook.databinding.FragmentTankFillBinding
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.model.TankFill

class TankFillFragment : Fragment() {

    private val binding by lazy {
        FragmentTankFillBinding.inflate(layoutInflater)
    }
    private val viewModel: TankFillViewModel by viewModel {
        parametersOf(carId)
    }
    private val carId by lazy { arguments?.getLong(CAR_ID_EXTRA) }
    private val petrolUnit by lazy { arguments?.getString(PETROL_UNIT_EXTRA) }
    private val adapter by lazy {
        TankFillAdapter(
            { adapterClick(it) },
            getPetrolUnitFromName(petrolUnit)
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
            adapter = this@TankFillFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }

        viewModel.tankFillAll.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun adapterClick(tankFill: TankFill) {
        Toast.makeText(requireContext(), "${tankFill.quantity}", Toast.LENGTH_LONG).show()
    }

    companion object {
        const val CAR_ID_EXTRA = "TankFillFragment.CAR_ID_EXTRA"
        const val PETROL_UNIT_EXTRA = "TankFillFragment.PETROL_UNIT_EXTRA"

        fun newInstance(carId: Long, petrolUnit: PetrolUnitEnum) = TankFillFragment().apply {
            arguments = bundleOf(
                CAR_ID_EXTRA to carId,
                PETROL_UNIT_EXTRA to petrolUnit.name
            )
        }
    }
}
