package com.example.razashop.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.razashop.R
import com.example.razashop.adapters.BestDealsAdapter
import com.example.razashop.adapters.BestProductsAdapters
import com.example.razashop.adapters.SpecialProductsAdapter
import com.example.razashop.databinding.FragmentMainCategoryBinding
import com.example.razashop.utils.Resource
import com.example.razashop.viewmodels.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val Tag = "Error"

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapters
    private val viewModel: MainCategoryViewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.specialProducts.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            hideLoading()
                            Log.d(Tag, it.message.toString())
                            Toast.makeText(
                                requireContext(),
                                " Sorry! Special products not available Please try again later ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            specialProductsAdapter.differ.submitList(it.data)
                            hideLoading()

                        }

                        is Resource.Unspecified -> {}
                    }

                }

            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.bestDealsProducts.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            hideLoading()
                            Log.d(Tag, it.message.toString())
                            Toast.makeText(
                                requireContext(),
                                " Sorry! Best Deals products not available Please try again later ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Loading -> {
                            showLoading()
                        }

                        is Resource.Success -> {
                            bestDealsAdapter.differ.submitList(it.data)
                            hideLoading()

                        }

                        is Resource.Unspecified -> {}
                    }

                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            hideLoading()
                            Log.d(Tag, it.message.toString())
                            Toast.makeText(
                                requireContext(),
                                " Sorry! Best Products not available Please try again later ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is Resource.Loading -> {
                            binding.bottomBar.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            bestProductsAdapter.differ.submitList(it.data)
                            binding.bottomBar.visibility = View.GONE

                        }

                        is Resource.Unspecified -> {}
                    }

                }

            }
        }


    }

    private fun setupBestProductsRv() {
        bestProductsAdapter = BestProductsAdapters()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter

            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as GridLayoutManager
                        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                        val totalItemCount = layoutManager.itemCount
                        if (lastVisibleItem >= totalItemCount - 2) {
                            viewModel.fetchBestProducts()
                        }
                    }
                }
            )
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter

        }
    }

    private fun hideLoading() {
        binding.loadingMainCategory.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loadingMainCategory.visibility = View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter

        }

    }


}