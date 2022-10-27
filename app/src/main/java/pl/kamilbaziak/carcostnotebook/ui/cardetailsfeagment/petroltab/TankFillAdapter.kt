package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.petroltab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewOdometerItemBinding
import pl.kamilbaziak.carcostnotebook.enums.PetrolUnitEnum
import pl.kamilbaziak.carcostnotebook.model.TankFill
import pl.kamilbaziak.carcostnotebook.shortcut
import pl.kamilbaziak.carcostnotebook.toDate

class TankFillAdapter(
    private val adapterClick: (TankFill) -> Unit,
    private val unit: PetrolUnitEnum
) :
    ListAdapter<TankFill, TankFillAdapter.TankFillViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TankFillViewHolder {
        val binding = ViewOdometerItemBinding.inflate(
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

    inner class TankFillViewHolder(private val bindining: ViewOdometerItemBinding) :
        RecyclerView.ViewHolder(bindining.root) {
        init {
            bindining.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val tankFill = getItem(position)
                        adapterClick(tankFill)
                    }
                }
            }
        }

        fun bind(tankFill: TankFill) {
            bindining.apply {
                textOdometer.text = root.context.getString(
                    R.string.odometer_item_value,
                    tankFill.quantity,
                    unit.shortcut()
                )
                textDate.text = tankFill.created.toDate()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TankFill>() {
        override fun areItemsTheSame(oldItem: TankFill, newItem: TankFill) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TankFill, newItem: TankFill): Boolean =
            oldItem == newItem
    }
}
