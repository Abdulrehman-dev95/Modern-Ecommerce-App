package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.razashop.R
import com.example.razashop.adapters.ColorsAdapter
import com.example.razashop.adapters.SizesAdapter
import com.example.razashop.adapters.ViewPagerImagesAdapter
import com.example.razashop.databinding.FragmentProductDetailsBinding
import com.example.razashop.utils.hideBottomNavigationView


class ProductDetailsFragment : Fragment(R.layout.fragment_product_details) {
    private lateinit var binding: FragmentProductDetailsBinding
    val args by navArgs<ProductDetailsFragmentArgs>()
    private val viewPagerAdapter by lazy { ViewPagerImagesAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }

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
            tvProductPrice.text = "Rs.${product.price}"
            tvProductDescription.text = product.description
            if (product.colors.isNullOrEmpty())
                tvProductColor.visibility = View.INVISIBLE
            if (product.sizes.isNullOrEmpty())
                tvProductSize.visibility = View.INVISIBLE

        }

        setupImagesViewPager()
        setupColorsRv()
        setupSizesRv()

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }


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