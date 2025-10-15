package com.example.razashop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.razashop.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    private var selectedPosition = -1

    inner class SizesViewHolder(val binding: SizeRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {
            binding.tvSize.text = size

            if (selectedPosition == position) {
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                }
            }

        }
    }

    val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SizesViewHolder {
        return SizesViewHolder(
            binding = SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: SizesViewHolder,
        position: Int
    ) {
        val size = differ.currentList[position]
        holder.bind(size, position)
        holder.itemView.setOnClickListener {
            if (selectedPosition == holder.adapterPosition) {
                // If the clicked item is already selected, deselect it
                selectedPosition = -1
                notifyItemChanged(holder.adapterPosition)
            } else {
                // If a different item is selected
                if (selectedPosition >= 0) {
                    notifyItemChanged(selectedPosition)
                }
                selectedPosition = holder.adapterPosition
                notifyItemChanged(selectedPosition)
                onItemClick?.invoke(size)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((String) -> Unit)? = null


}