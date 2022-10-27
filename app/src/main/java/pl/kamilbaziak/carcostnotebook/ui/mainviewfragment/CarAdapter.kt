package pl.kamilbaziak.carcostnotebook.ui.mainviewfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.databinding.ViewMainViewItemBinding
import pl.kamilbaziak.carcostnotebook.model.Car

class CarAdapter(
    private val listener: OnItemClickListener
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
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClicked(getItem(position))
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
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClicked(car: Car)
    }
}
