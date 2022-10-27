package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.odometertab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.databinding.ViewOdometerItemBinding
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Odometer

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

    //uzywamy viewBidingu do przekazywania kodu
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
                textOdometer.text = "${odometer.input} ${unit.name}"
                textDate.text = odometer.createdDateFormatted
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(odometer: Odometer)
    }

    class DiffCallback : DiffUtil.ItemCallback<Odometer>() {
        override fun areItemsTheSame(oldItem: Odometer, newItem: Odometer) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Odometer, newItem: Odometer): Boolean =
            oldItem == newItem
    }
}