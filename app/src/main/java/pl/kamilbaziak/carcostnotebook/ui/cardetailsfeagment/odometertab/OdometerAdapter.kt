package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewOdometerItemBinding
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.shortcut
import pl.kamilbaziak.carcostnotebook.toDate

class OdometerAdapter(
    private val adaperClick: (Odometer) -> Unit,
    private val unit: UnitEnum
) :
    ListAdapter<Odometer, OdometerAdapter.OdometerViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OdometerViewHolder {
        val binding = ViewOdometerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OdometerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OdometerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class OdometerViewHolder(private val bidining: ViewOdometerItemBinding) :
        RecyclerView.ViewHolder(bidining.root) {
        init {
            bidining.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val odometer = getItem(position)
                        adaperClick(odometer)
                    }
                }
            }
        }

        fun bind(odometer: Odometer) {
            bidining.apply {
                textOdometer.text = root.context.getString(
                    R.string.odometer_item_value,
                    odometer.input,
                    unit.shortcut()
                )
                textDate.text = odometer.created.toDate()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Odometer>() {
        override fun areItemsTheSame(oldItem: Odometer, newItem: Odometer) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Odometer, newItem: Odometer): Boolean =
            oldItem == newItem
    }
}
