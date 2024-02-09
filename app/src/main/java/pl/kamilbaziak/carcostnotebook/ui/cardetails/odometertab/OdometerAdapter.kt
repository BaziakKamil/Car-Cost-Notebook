package pl.kamilbaziak.carcostnotebook.ui.cardetails.odometertab

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
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
    private val editOdometer: (Odometer) -> Unit,
    private val deleteOdometer: (Odometer) -> Unit,
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

    inner class OdometerViewHolder(private val binding: ViewOdometerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val popMenu = PopupMenu(binding.root.context, binding.root).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            deleteOdometer(getItem(adapterPosition))
                        }
                        true
                    }
                    R.id.edit -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            editOdometer(getItem(adapterPosition))
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

        fun bind(odometer: Odometer) {
            binding.apply {
                popMenu.menu.getItem(1).isVisible = odometer.canBeDeleted
                textOdometer.text = root.context.getString(
                    R.string.odometer_item_value,
                    odometer.input.toString(),
                    unit.shortcut()
                )
                textDate.text = odometer.created.toDate()
                textDescription.apply {
                    if (!odometer.description.isNullOrEmpty()) {
                        isVisible = true
                        text = odometer.description
                    } else {
                        isVisible = false
                    }
                }
                imageMore.setOnClickListener {
                    popMenu.show()
                }
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
