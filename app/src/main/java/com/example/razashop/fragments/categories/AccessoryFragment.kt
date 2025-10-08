package com.example.razashop.fragments.categories

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.razashop.data.Categories
import com.example.razashop.utils.Resource
import com.example.razashop.viewmodels.CategoryViewModel
import com.example.razashop.viewmodels.ViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class AccessoryFragment: BaseCategoryFragment() {
    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore


    val viewModel: CategoryViewModel by viewModels {
        ViewModelFactory(firebaseFirestore, Categories.Accessory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestProducts.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            hideBestProductsLoading()
                        }

                        is Resource.Loading -> {
                            showBestProductsLoading()
                        }
                        is Resource.Success -> {
                            bestProductsAdapter.differ.submitList(it.data)
                            hideBestProductsLoading()
                        }

                        is Resource.Unspecified -> {}
                    }
                }

            }


        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offerProducts.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            hideOfferLoading()
                        }

                        is Resource.Loading -> {
                            showOfferLoading()
                        }
                        is Resource.Success -> {
                            bestProductsAdapter.differ.submitList(it.data)
                            showOfferLoading()
                        }

                        is Resource.Unspecified -> {}
                    }
                }

            }


        }


    }
}