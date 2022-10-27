package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewOdometerItemBinding
import pl.kamilbaziak.carcostnotebook.enums.UnitEnum
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.shortcut
import pl.kamilbaziak.carcostnotebook.toDate

class MaintenanceAdapter(
    private val adaperClick: (Maintenance) -> Unit
) :
    ListAdapter<Maintenance, MaintenanceAdapter.MaintenanceViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val binding = ViewOdometerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MaintenanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class MaintenanceViewHolder(private val bidining: ViewOdometerItemBinding) :
        RecyclerView.ViewHolder(bidining.root) {
        init {
            bidining.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val maintenance = getItem(position)
                        adaperClick(maintenance)
                    }
                }
            }
        }

        fun bind(maintenance: Maintenance) {
            bidining.apply {
                textOdometer.text = maintenance.name
                textDate.text = maintenance.created.toDate()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Maintenance>() {
        override fun areItemsTheSame(oldItem: Maintenance, newItem: Maintenance) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Maintenance, newItem: Maintenance): Boolean =
            oldItem == newItem
    }
}
