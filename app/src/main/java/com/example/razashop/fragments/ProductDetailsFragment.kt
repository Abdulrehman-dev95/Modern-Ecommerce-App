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
import com.example.razashop.adapters.ColorsAdapter
import com.example.razashop.adapters.SizesAdapter
import com.example.razashop.adapters.ViewPagerImagesAdapter
import com.example.razashop.data.CartProduct
import com.example.razashop.databinding.FragmentProductDetailsBinding
import com.example.razashop.utils.Resource
import com.example.razashop.utils.afterDiscount
import com.example.razashop.utils.hideBottomNavigationView
import com.example.razashop.viewmodels.ProductDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductDetailsFragment : Fragment(R.layout.fragment_product_details) {
    private lateinit var binding: FragmentProductDetailsBinding
    val args by navArgs<ProductDetailsFragmentArgs>()
    private val viewPagerAdapter by lazy { ViewPagerImagesAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private val viewModel: ProductDetailsViewModel by viewModels<ProductDetailsViewModel>()
    private var selectedColor: Int? = null
    private var selectedSize: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigationView()

        val product = args.product
        binding.apply {
            tvProductName.text = product.name
            val discountedPrice = product.offerPercentage?.let { product.price.afterDiscount(it) }
            tvProductPrice.text = context?.getString(
                R.string.rs,
                discountedPrice?.toString() ?: product.price.toString()
            )
            tvProductDescription.text = product.description
            if (product.colors.isNullOrEmpty())
                tvProductColor.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE
            imageClose.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        setupImagesViewPager()
        setupColorsRv()
        setupSizesRv()

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateToCart(CartProduct(product, 1, selectedColor, selectedSize))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.addToCart.collect {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonAddToCart.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.buttonAddToCart.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Product Added to cart",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        is Resource.Error -> {
                            binding.buttonAddToCart.revertAnimation()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        is Resource.Unspecified -> {}
                    }
                }
            }
        }


    }

    private fun setupSizesRv() {
        binding.apply {
            rvSize.adapter = sizesAdapter
            rvSize.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupColorsRv() {
        binding.apply {
            rvColors.adapter = colorsAdapter
            rvColors.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupImagesViewPager() {
        binding.apply {
            viewpagerProductImages.adapter = viewPagerAdapter
        }
    }


}