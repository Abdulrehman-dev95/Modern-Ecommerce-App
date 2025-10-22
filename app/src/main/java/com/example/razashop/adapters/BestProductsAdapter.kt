package com.example.razashop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.razashop.R
import com.example.razashop.data.Product
import com.example.razashop.databinding.ProductRvItemBinding
import com.example.razashop.utils.priceAfterDiscount

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(binding.imgProduct)
                tvName.text = product.name
                if (product.offerPercentage != null) {
                    tvNewPrice.visibility = android.view.View.VISIBLE
                    val newPrice = product.offerPercentage.priceAfterDiscount(product.price)
                    tvNewPrice.text = itemView.context.getString(R.string.new_price, newPrice)
                    tvPrice.text = itemView.context.getString(R.string.rs, product.price.toString())
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvNewPrice.visibility = android.view.View.INVISIBLE
                    tvPrice.text = itemView.context.getString(R.string.rs, product.price.toString())
                }
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
    ): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
            )
        )
    }

    override fun onBindViewHolder(
        holder: BestProductsViewHolder,
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