package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewOdometerItemBinding
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.toDate

class MaintenanceAdapter(
    private val editMaintenance: (Maintenance) -> Unit,
    private val deleteMaintenance: (Maintenance) -> Unit
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

    inner class MaintenanceViewHolder(private val binding: ViewOdometerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val popMenu = PopupMenu(binding.root.context, binding.root).apply {
            setOnMenuItemClickListener {  item ->
                when (item.itemId) {
                    R.id.delete -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            deleteMaintenance(getItem(adapterPosition))
                        }
                        true
                    }
                    R.id.edit -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            editMaintenance(getItem(adapterPosition))
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

        fun bind(maintenance: Maintenance) {
            binding.apply {
                textOdometer.text = maintenance.name
                textDate.text = maintenance.created.toDate()
                imageMore.setOnClickListener {
                    popMenu.show()
                }
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
