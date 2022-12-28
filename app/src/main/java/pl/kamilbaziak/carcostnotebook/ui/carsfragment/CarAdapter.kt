package pl.kamilbaziak.carcostnotebook.ui.carsfragment

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewMainViewItemBinding
import pl.kamilbaziak.carcostnotebook.model.Car

class CarAdapter(
    private val adapterClick: (Car) -> Unit,
    private val editCar: (Car) -> Unit,
    private val deleteCar: (Car) -> Unit
) : ListAdapter<Car, CarAdapter.CarViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ViewMainViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CarViewHolder(private val binding: ViewMainViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val popMenu = PopupMenu(binding.root.context, binding.root).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            deleteCar(getItem(adapterPosition))
                        }
                        true
                    }
                    R.id.edit -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            editCar(getItem(adapterPosition))
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
                root.apply {
                    setOnClickListener {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            adapterClick(getItem(adapterPosition))
                        }
                    }
                    setOnLongClickListener {
                        popMenu.show()
                        true
                    }
                }
            }
        }

        fun bind(car: Car) {
            binding.apply {
                textCarBrand.text = car.brand
                textCarModel.text = car.model
                textCarYear.text = car.year.toString()
                textCarLicencePlate.text = car.licensePlate
                imageMore.setOnClickListener { popMenu.show() }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem == newItem
    }
}
