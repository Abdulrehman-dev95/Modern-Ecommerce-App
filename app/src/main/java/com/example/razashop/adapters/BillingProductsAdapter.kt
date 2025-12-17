package com.example.razashop.adapters
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.razashop.R
import com.example.razashop.data.CartProduct
import com.example.razashop.databinding.BillingProductsRvItemBinding
import com.example.razashop.utils.afterDiscount

class BillingProductsAdapter() :
    RecyclerView.Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {

    class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                if (cartProduct.product.images.isNotEmpty()) {
                    Glide.with(itemView).load(cartProduct.product.images[0])
                        .into(binding.imageCartProduct)
                }
                tvProductCartName.text = cartProduct.product.name
                tvBillingProductQuantity.text = cartProduct.quantity.toString()
                val discountedPrice = cartProduct.product.offerPercentage?.let {
                    cartProduct.product.price.afterDiscount(it)
                }

                tvProductCartPrice.text = itemView.context.getString(
                    R.string.new_price,
                    discountedPrice?.toString() ?: cartProduct.product.price.toString()
                )

                imageCartProductColor.setImageDrawable(
                    (cartProduct.selectedColor ?: Color.TRANSPARENT).toDrawable()
                )

                tvCartProductSize.text = cartProduct.selectedSize ?: "".also {
                    imageCartProductSize.setImageDrawable(Color.TRANSPARENT.toDrawable())
                }
            }
        }
    }


    val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(
            oldItem: CartProduct,
            newItem: CartProduct
        ): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(
            oldItem: CartProduct,
            newItem: CartProduct
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            binding = BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(
        holder: BillingProductsViewHolder,
        position: Int
    ) {
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}