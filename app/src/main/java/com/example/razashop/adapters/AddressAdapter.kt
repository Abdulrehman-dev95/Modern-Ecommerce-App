package com.example.razashop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.razashop.R
import com.example.razashop.data.Address
import com.example.razashop.databinding.AddressRvItemBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, isSelected: Boolean) {
            binding.buttonAddress.text = address.addressTitle
            if (isSelected) {
                binding.buttonAddress.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.buttonAddress.context,
                        R.color.g_blue
                    )
                )
            } else {
                binding.buttonAddress.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.buttonAddress.context,
                        R.color.g_white
                    )
                )
            }
        }
    }

    val diffUtil = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(
            oldItem: Address,
            newItem: Address
        ): Boolean {
            return oldItem.addressTitle == newItem.addressTitle
                    && newItem.fullName == oldItem.fullName
        }

        override fun areContentsTheSame(
            oldItem: Address,
            newItem: Address
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressViewHolder {
        return AddressViewHolder(
            binding = AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    var selectedAddress = -1
    override fun onBindViewHolder(
        holder: AddressViewHolder,
        position: Int
    ) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)
        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0)
                notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onclick?.invoke(address)
        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onclick: ((Address) -> Unit)? = null


}

