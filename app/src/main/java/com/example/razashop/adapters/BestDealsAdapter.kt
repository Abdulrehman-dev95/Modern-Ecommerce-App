package com.example.razashop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.razashop.data.Product
import com.example.razashop.databinding.BestDealsRvItemBinding
import java.util.Locale

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {
    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {

            binding.apply {

                Glide.with(itemView).load(product.images[0]).into(binding.imgBestDeal)
                tvDealProductName.text = product.name
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text =
                        String.format(locale = Locale.getDefault(), "%.2f", priceAfterOffer)

                }
                tvOldPrice.text = product.price.toString()


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
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(
        holder: BestDealsViewHolder,
        position: Int
    ) {

        val product = differ.currentList[position]
        holder.bind(product)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}