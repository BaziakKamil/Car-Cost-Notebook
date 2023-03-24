package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewTankFillItemBinding
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.model.TankFill
import pl.kamilbaziak.carcostnotebook.shortcut
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits

class TankFillAdapter(
    private val editTankFill: (TankFill) -> Unit,
    private val deleteTankFill: (TankFill) -> Unit,
    private val unit: PetrolUnitEnum,
) : ListAdapter<Pair<TankFill, Odometer?>, TankFillAdapter.TankFillViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TankFillViewHolder {
        val binding = ViewTankFillItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TankFillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TankFillViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class TankFillViewHolder(private val binding: ViewTankFillItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val popMenu = PopupMenu(binding.root.context, binding.root).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            deleteTankFill(getItem(adapterPosition).first)
                        }
                        true
                    }
                    R.id.edit -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            editTankFill(getItem(adapterPosition).first)
                        }
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.more_menu)
            gravity = Gravity.END
        }

        init {
            binding.apply {
                root.setOnLongClickListener {
                    popMenu.show()
                    true
                }
            }
        }

        fun bind(tankFill: Pair<TankFill, Odometer?>) {
            binding.apply {
                val ctx = root.context
                val mileageUnit =
                    tankFill.second?.unit?.shortcut() ?: UnitEnum.Kilometers.shortcut()
                textPetrolStation.text = tankFill.first.petrolStation
                textDate.text = tankFill.first.created.toDate()

                textOdometer.text = tankFill.second?.let {
                    ctx.getString(
                        R.string.odometer_item_value,
                        it.input.toTwoDigits(),
                        it.unit.shortcut()
                    )
                } ?: "-"

                textAmountAndPrice.text = tankFill.first.petrolPrice?.let {
                    ctx.getString(
                        R.string.petrol_amount_and_price_value,
                        tankFill.first.quantity.toTwoDigits(),
                        unit.shortcut(),
                        tankFill.first.petrolPrice!!.toTwoDigits(),
                        "${ctx.getString(R.string.pln_currency)} / ${unit.shortcut()}"
                    )
                } ?: ctx.getString(
                    R.string.petrol_amount,
                    tankFill.first.quantity.toTwoDigits(),
                    unit.shortcut()
                )

                textComputed.text = tankFill.first.computerReading?.let {
                    ctx.getString(
                        R.string.from_computed,
                        tankFill.first.computerReading!!.toTwoDigits(),
                        unit.shortcut() + "/100" + mileageUnit
                    )
                } ?: "-"

                textTotalPrice.text = ctx.getString(
                    R.string.total,
                    calculateTotalPrice(
                        tankFill.first.quantity,
                        tankFill.first.petrolPrice
                    ).toTwoDigits(),
                    ctx.getString(R.string.pln_currency)
                )

                textFromLastTankFill.text = ctx.getString(
                    R.string.from_last_fill,
                    calculatePetrolUsage(
                        tankFill.first.quantity,
                        tankFill.first.distanceFromLastTankFill
                    ).toTwoDigits(),
                    unit.shortcut() + "/100" + mileageUnit
                )

                textDistanceOnFill.text = tankFill.first.distanceFromLastTankFill?.let {
                    ctx.getString(
                        R.string.distance_driven,
                        tankFill.first.distanceFromLastTankFill!!.toTwoDigits(),
                        mileageUnit
                    )
                } ?: "-"
                imageMore.setOnClickListener {
                    popMenu.show()
                }
            }
        }

        private fun calculatePetrolUsage(petrol: Double, distance: Double?): Double =
            distance?.let {
                (petrol / it) * 100
            } ?: 0.0

        fun calculateTotalPrice(liters: Double, price: Double?): Double = price?.let {
            liters * it
        } ?: 0.0
    }

    class DiffCallback : DiffUtil.ItemCallback<Pair<TankFill, Odometer?>>() {
        override fun areItemsTheSame(
            oldItem: Pair<TankFill, Odometer?>,
            newItem: Pair<TankFill, Odometer?>
        ) =
            oldItem.first.id == newItem.first.id

        override fun areContentsTheSame(
            oldItem: Pair<TankFill, Odometer?>,
            newItem: Pair<TankFill, Odometer?>
        ): Boolean =
            oldItem == newItem
    }
}
