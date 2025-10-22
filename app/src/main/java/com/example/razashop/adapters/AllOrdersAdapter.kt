package com.example.razashop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.razashop.R
import com.example.razashop.data.Order
import com.example.razashop.data.OrderStatus
import com.example.razashop.databinding.OrderItemBinding

class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.AllOrdersViewHolder>() {

    inner class AllOrdersViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.orderDate
                val resources = itemView.resources

                val colorDrawable = when (OrderStatus.fromStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> {
                        getColor(resources, R.color.g_orange_yellow, null).toDrawable()
                    }
                    is OrderStatus.Confirmed -> {
                        resources.getColor(R.color.g_green, null).toDrawable()
                    }
                    is OrderStatus.Delivered -> {
                        resources.getColor(R.color.g_green, null).toDrawable()
                    }
                    is OrderStatus.Shipped -> {
                        resources.getColor(R.color.g_green, null).toDrawable()
                    }
                    is OrderStatus.Canceled -> {
                        resources.getColor(R.color.g_red, null).toDrawable()
                    }
                    is OrderStatus.Returned -> {
                        resources.getColor(R.color.g_red, null).toDrawable()
                    }
                }
                imageOrderState.setImageDrawable(colorDrawable)
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrdersViewHolder {
        return AllOrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AllOrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Order) -> Unit)? = null
}