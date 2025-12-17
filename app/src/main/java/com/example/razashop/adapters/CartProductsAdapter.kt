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
import com.example.razashop.databinding.CartProductItemBinding
import com.example.razashop.utils.afterDiscount

class CartProductsAdapter() : RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {
    class CartProductsViewHolder(val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProduct) {
            binding.apply {

                if (cartProduct.product.images.isNotEmpty()) {
                    Glide.with(itemView).load(cartProduct.product.images[0])
                        .into(binding.imageCartProduct)
                }
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

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
    ): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CartProductsViewHolder,
        position: Int
    ) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(product)
        }
        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

    var onItemClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null


}