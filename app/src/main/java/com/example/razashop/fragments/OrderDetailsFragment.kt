package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.razashop.R
import com.example.razashop.adapters.BillingProductsAdapter
import com.example.razashop.data.OrderStatus
import com.example.razashop.databinding.FragmentOrderDetailBinding
import com.example.razashop.utils.VerticalItemDecoration

class OrderDetailsFragment : Fragment(R.layout.fragment_order_detail) {
    lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy {
        BillingProductsAdapter()
    }
    private val args by navArgs<OrderDetailsFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order
        binding.apply {
            rvProducts.adapter = billingProductsAdapter
            rvProducts.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            rvProducts.addItemDecoration(VerticalItemDecoration())


            tvOrderId.text = getString(R.string.order ,order.orderId)

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )
            val currentOrderState = when(OrderStatus.fromStatus(order.orderStatus)){
                OrderStatus.Ordered -> 0
                OrderStatus.Confirmed -> 1
                OrderStatus.Shipped -> 2
                OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState, false)
            if (currentOrderState == 3){
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text =
                getString(R.string.order_address, order.address.address, order.address.city)
            tvPhoneNumber.text = order.address.phone

            tvTotalPrice.text = getString(R.string.rs, order.totalPrice.toString())


        }

        billingProductsAdapter.differ.submitList(order.products)

    }


}