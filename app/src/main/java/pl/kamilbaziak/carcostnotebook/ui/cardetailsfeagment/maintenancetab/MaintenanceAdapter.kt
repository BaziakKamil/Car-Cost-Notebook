package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment.maintenancetab

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewMaintenanceItemBinding
import pl.kamilbaziak.carcostnotebook.databinding.ViewOdometerItemBinding
import pl.kamilbaziak.carcostnotebook.model.Maintenance
import pl.kamilbaziak.carcostnotebook.toDate
import pl.kamilbaziak.carcostnotebook.toTwoDigits

class MaintenanceAdapter(
    private val editMaintenance: (Maintenance) -> Unit,
    private val deleteMaintenance: (Maintenance) -> Unit
) :
    ListAdapter<Maintenance, MaintenanceAdapter.MaintenanceViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val binding = ViewMaintenanceItemBinding.inflate(
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

    inner class MaintenanceViewHolder(private val binding: ViewMaintenanceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val popMenu = PopupMenu(binding.root.context, binding.root).apply {
            setOnMenuItemClickListener { item ->
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
            val ctx = binding.root.context
            binding.apply {

                textMaintenanceName.text = maintenance.name
                textMaintenanceDate.text = maintenance.created.toDate()
                maintenance.price?.let {
                    textMaintenancePrice.text = ctx.getString(
                        R.string.total,
                        it.toTwoDigits(),
                        ctx.getString(R.string.pln_currency)
                    )
                } ?: run { textMaintenanceName.isVisible = false }
                maintenance.description?.let {
                    textMaintenanceDescription.text = it
                } ?: run { textMaintenanceDescription.isVisible = false }
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
