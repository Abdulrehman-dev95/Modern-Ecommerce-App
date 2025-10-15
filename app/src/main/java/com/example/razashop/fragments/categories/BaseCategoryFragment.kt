package com.example.razashop.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.razashop.R
import com.example.razashop.adapters.BestProductsAdapter
import com.example.razashop.databinding.FragmentBaseCategoryBinding
import com.example.razashop.utils.showBottomNavigationView
import com.example.razashop.viewmodels.CategoryViewModel

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy {
        BestProductsAdapter()
    }
    protected val bestProductsAdapter: BestProductsAdapter by lazy {
        BestProductsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOfferRv()
        setupBestProductsRv()
    }

     fun hideOfferLoading() {
        binding.offerProgressBar.visibility = View.INVISIBLE
    }

     fun showOfferLoading() {
        binding.offerProgressBar.visibility = View.VISIBLE
    }

    fun hideBestProductsLoading() {
        binding.bestProgressBar.visibility = View.INVISIBLE
    }

    fun showBestProductsLoading() {
        binding.bestProgressBar.visibility = View.VISIBLE
    }

     fun setupBestProductsRv() {
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

    private fun setupOfferRv() {
        binding.rvOffer.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }

}

