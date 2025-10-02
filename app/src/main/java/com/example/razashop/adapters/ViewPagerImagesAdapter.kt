package com.example.razashop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.razashop.databinding.ViewpagerImageItemBinding

class ViewPagerImagesAdapter :
    RecyclerView.Adapter<ViewPagerImagesAdapter.ViewPagerImagesViewHolder>() {

    inner class ViewPagerImagesViewHolder(val binding: ViewpagerImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerImagesViewHolder {
        return ViewPagerImagesViewHolder(
            binding = ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewPagerImagesViewHolder,
        position: Int
    ) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}