package com.example.razashop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.razashop.databinding.ColorRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {
    private var selectedPosition = -1

    inner class ColorViewHolder(val binding: ColorRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(color: Int, position: Int) {
            val imageDrawable = color.toDrawable()
            binding.imageColor.setImageDrawable(imageDrawable)

            if (selectedPosition == position) {
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                    imageSelected.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                    imageSelected.visibility = View.INVISIBLE
                }
            }

        }
    }

    val diffUtil = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColorViewHolder {
        return ColorViewHolder(
            binding = ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ColorViewHolder,
        position: Int
    ) {
        val color = differ.currentList[position]
        holder.bind(color, position)
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
                onItemClick?.invoke(color)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Int) -> Unit)? = null
}


