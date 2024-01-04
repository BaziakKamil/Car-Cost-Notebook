package pl.kamilbaziak.carcostnotebook.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.kamilbaziak.carcostnotebook.databinding.ViewItemRadioButtonBinding

class SimpleSingleItemAdapter(
    private val getItem: (title: String) -> Unit
) : ListAdapter<String, SimpleSingleItemAdapter.SimpleSingleItemViewHolder>(DiffCallback()) {

    private var checkedItem: CompoundButton? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleSingleItemViewHolder = SimpleSingleItemViewHolder(
        ViewItemRadioButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SimpleSingleItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SimpleSingleItemViewHolder(
        private val binding: ViewItemRadioButtonBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String) = binding.apply {
            itemRadioButton.apply {
                setOnCheckedChangeListener(checkedChangeListener)
                text = title
            }
            root.setOnClickListener {
                getItem(getItem(adapterPosition))
            }
        }
    }

    private val checkedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            checkedItem?.apply { setChecked(!isChecked) }
            checkedItem = buttonView.apply { setChecked(isChecked) }
        }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }
}