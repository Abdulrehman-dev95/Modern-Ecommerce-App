package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.razashop.R
import com.example.razashop.adapters.BestProductsAdapter
import com.example.razashop.databinding.FragmentSearchBinding
import com.example.razashop.utils.Resource
import com.example.razashop.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var adapter: BestProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchResults.collectLatest {
                    when (it) {
                        is Resource.Error<*> -> {
                            binding.progressBar.visibility = View.GONE
                        }

                        is Resource.Loading<*> -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Resource.Success<*> -> {
                            binding.progressBar.visibility = View.GONE
                            adapter.differ.submitList(it.data)
                        }

                        is Resource.Unspecified<*> -> {
                            binding.progressBar.visibility = View.GONE
                            if (binding.edSearch.text.toString().trim().isEmpty()) {
                                adapter.differ.submitList(emptyList())
                            }
                        }
                    }
                }
            }
        }

        /**
         * It is not used in the current version of the app.
         */
//        binding.edSearch.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {}
//
//            override fun beforeTextChanged(
//                s: CharSequence?,
//                start: Int,
//                count: Int,
//                after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                s: CharSequence?,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
//                searchViewModel.searchProducts(s.toString())
//            }
//        })

        binding.edSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.edSearch.text.toString().trim()

                if (query.isNotEmpty()) {
                    searchViewModel.searchProducts(query)
                }

                hideKeyboard()
                true
            } else {
                false
            }
        }

        adapter.onItemClick = {
            val action = SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.edSearch.windowToken, 0)
    }


    private fun setupRecyclerView() {
        adapter = BestProductsAdapter()
        binding.rvSearch.adapter = adapter
        binding.rvSearch.layoutManager = GridLayoutManager(requireContext(), 2)

    }


}