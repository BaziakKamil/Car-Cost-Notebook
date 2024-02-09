package pl.kamilbaziak.carcostnotebook.ui.cars

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.R
import pl.kamilbaziak.carcostnotebook.databinding.ViewCarItemBinding
import pl.kamilbaziak.carcostnotebook.model.Car
import pl.kamilbaziak.carcostnotebook.model.Odometer
import pl.kamilbaziak.carcostnotebook.shortcut
import pl.kamilbaziak.carcostnotebook.toTwoDigits

class CarListAdapter(
    private val openCarDetails: (Car) -> Unit,
    private val editCar: (Car) -> Unit,
    private val deleteCar: (Car) -> Unit
) : ListAdapter<Pair<Car, Odometer?>, CarListAdapter.CarViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ViewCarItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CarViewHolder(private val binding: ViewCarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val popMenu = PopupMenu(binding.root.context, binding.root).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            deleteCar(getItem(adapterPosition).first)
                        }
                        true
                    }
                    R.id.edit -> {
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            editCar(getItem(adapterPosition).first)
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
                            openCarDetails(getItem(adapterPosition).first)
                        }
                    }
                    setOnLongClickListener {
                        popMenu.show()
                        true
                    }
                }
            }
        }

        fun bind(item: Pair<Car, Odometer?>) {
            val ctx = binding.root.context
            val car = item.first
            binding.apply {
                textCarBrand.text = car.brand
                textCarModel.text = car.model
                textCarYear.text = car.year.toString()
                textCarLicencePlate.text = car.licensePlate
                item.second?.let {
                    textCarLastOdometer.apply {
                        isVisible = true
                        text = ctx.getString(
                            R.string.odometer_item_value,
                            it.input.toTwoDigits(),
                            it.unit.shortcut()
                        )
                    }
                } ?: kotlin.run { textCarLastOdometer.isVisible = false }
                textCarDescription.apply {
                    isVisible = car.description.isNotEmpty()
                    text = car.description
                }
                imageMore.setOnClickListener { popMenu.show() }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Pair<Car, Odometer?>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Car, Odometer?>,
            newItem: Pair<Car, Odometer?>
        ) =
            oldItem.first.id == newItem.first.id

        override fun areContentsTheSame(
            oldItem: Pair<Car, Odometer?>,
            newItem: Pair<Car, Odometer?>
        ): Boolean =
            oldItem == newItem
    }
}
