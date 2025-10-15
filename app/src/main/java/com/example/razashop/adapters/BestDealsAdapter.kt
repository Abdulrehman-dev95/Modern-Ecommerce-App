package com.example.razashop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.razashop.R
import com.example.razashop.data.Product
import com.example.razashop.databinding.BestDealsRvItemBinding
import com.example.razashop.utils.priceAfterDiscount

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {
    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {

            binding.apply {

                Glide.with(itemView).load(product.images[0]).into(binding.imgBestDeal)
                tvDealProductName.text = product.name
                product.offerPercentage?.let {
                    tvNewPrice.text = it.priceAfterDiscount(product.price)
                    tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                tvNewPrice.visibility = View.INVISIBLE
                tvOldPrice.text = itemView.context.getString(R.string.rs, product.price.toString())

            }

        }
    }

    val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Product,
            newItem: Product
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BestDealsViewHolder {

        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: BestDealsViewHolder,
        position: Int
    ) {

        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Product) -> Unit)? = null


}