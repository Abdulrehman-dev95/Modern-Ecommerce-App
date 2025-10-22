package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.razashop.R
import com.example.razashop.adapters.CartProductsAdapter
import com.example.razashop.databinding.FragmentCartBinding
import com.example.razashop.firebase.FireBaseCommon
import com.example.razashop.utils.Resource
import com.example.razashop.utils.VerticalItemDecoration
import com.example.razashop.utils.showAlertDialog
import com.example.razashop.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartProductsAdapter
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var totalPrice = 0F
        binding.apply {
            cartAdapter = CartProductsAdapter()
            rvCart.adapter = cartAdapter
            rvCart.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvCart.addItemDecoration(VerticalItemDecoration())

        }



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collect {
                    when (it) {
                        is Resource.Success -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            if (it.data!!.isNotEmpty()) {
                                binding.layoutCarEmpty.visibility = View.GONE
                                binding.rvCart.visibility = View.VISIBLE
                                binding.totalBoxContainer.visibility = View.VISIBLE
                                binding.buttonCheckout.visibility = View.VISIBLE

                                cartAdapter.differ.submitList(it.data)
                            } else {
                                binding.rvCart.visibility = View.GONE
                                binding.totalBoxContainer.visibility = View.GONE
                                binding.buttonCheckout.visibility = View.GONE
                                binding.layoutCarEmpty.visibility = View.VISIBLE
                            }
                        }

                        is Resource.Error -> {
                            binding.progressbarCart.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                it.message ?: "Error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Loading -> {
                            binding.progressbarCart.visibility = View.VISIBLE
                        }

                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsPrice.collectLatest { price ->
                    price?.let {
                        totalPrice = it
                        binding.tvTotalPrice.text = context?.getString(R.string.rs, it.toString())

                    }
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteDialog.collectLatest {
                    showAlertDialog(
                        title = "Delete item from cart",
                        message = "Do you want to delete this item from your cart?"
                    ) {
                        viewModel.deleteCartProduct(it)
                    }
                }
            }
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FireBaseCommon.QualityChanging.DECREASE)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FireBaseCommon.QualityChanging.INCREASE)
        }
        cartAdapter.onItemClick = {
            val b = Bundle().apply {
                putParcelable("product", it.product)
            }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
                totalPrice = totalPrice,
                cartAdapter.differ.currentList.toTypedArray(),
                true
            )
            findNavController().navigate(action)

        }

    }

}