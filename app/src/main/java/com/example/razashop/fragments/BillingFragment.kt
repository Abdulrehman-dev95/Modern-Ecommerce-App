package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.razashop.R
import com.example.razashop.adapters.AddressAdapter
import com.example.razashop.adapters.BillingProductsAdapter
import com.example.razashop.data.Address
import com.example.razashop.data.CartProduct
import com.example.razashop.data.Order
import com.example.razashop.data.OrderStatus
import com.example.razashop.databinding.FragmentBillingBinding
import com.example.razashop.utils.HorizontalItemDecoration
import com.example.razashop.utils.Resource
import com.example.razashop.utils.showAlertDialog
import com.example.razashop.viewmodels.BillingViewModel
import com.example.razashop.viewmodels.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillingFragment : Fragment(R.layout.fragment_billing) {
    lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy {
        AddressAdapter()
    }

    private val billingProductsAdapter by lazy {
        BillingProductsAdapter()
    }

    private val billingViewModel by viewModels<BillingViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var totalPrice = 0F
    private var billingProducts = emptyList<CartProduct>()
    private var selectedAddress: Address? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalPrice = args.totalPrice
        billingProducts = args.billingProducts.toList()

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!args.ispayment) {
            binding.buttonPlaceOrder.visibility = View.INVISIBLE
            binding.totalBoxContainer.visibility = View.INVISIBLE
            binding.middleLine.visibility = View.INVISIBLE
            binding.bottomLine.visibility = View.INVISIBLE
        }


        binding.apply {
            rvAddress.adapter = addressAdapter
            rvAddress.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvAddress.addItemDecoration(HorizontalItemDecoration())
            billingProductsAdapter.differ.submitList(billingProducts)
            rvProducts.adapter = billingProductsAdapter
            rvProducts.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvProducts.addItemDecoration(HorizontalItemDecoration())
            imageAddAddress.setOnClickListener {
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
            }
            tvTotalPrice.text = getString(R.string.new_price, totalPrice.toString())

        }

        addressAdapter.onclick = {
            selectedAddress = it
        }



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                billingViewModel.getAddress.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarAddress.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            binding.progressbarAddress.visibility = View.GONE
                            addressAdapter.differ.submitList(it.data)

                        }

                        is Resource.Error -> {
                            binding.progressbarAddress.visibility = View.GONE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }


                        else -> Unit
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                orderViewModel.order.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonPlaceOrder.startAnimation()
                        }

                        is Resource.Success<*> -> {
                            binding.buttonPlaceOrder.revertAnimation()
                            findNavController().navigateUp()
                            Snackbar.make(
                                requireView(),
                                "Your order was placed",
                                Snackbar.LENGTH_LONG
                            ).show()

                        }


                        is Resource.Error -> {
                            binding.buttonPlaceOrder.revertAnimation()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> Unit
                    }
                }
            }
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select the address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val order = Order(
                orderStatus = OrderStatus.Ordered.status,
                totalPrice = totalPrice,
                products = billingProducts,
                address = selectedAddress!!
            )

            showAlertDialog(
                title = "Order items",
                message = "Do you want to order your cart items?"
            ) {
                orderViewModel.placeOrder(order)
            }


        }

    }


}