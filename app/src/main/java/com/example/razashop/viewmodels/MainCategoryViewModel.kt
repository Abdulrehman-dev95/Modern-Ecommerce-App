package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razashop.data.Product
import com.example.razashop.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts = _specialProducts.asStateFlow()

    private val _bestDealsProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts = _bestDealsProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    private val bestProductsList = mutableListOf<Product>()
    private var lastVisibleDocument: DocumentSnapshot? = null
    private var isLoadingMore = false
    private val pageSize = 10L


    init {
        fetchSpecialProducts()
        fetchBestDealsProducts()
        fetchBestProducts()

    }

    fun fetchBestProducts() {
        if (isLoadingMore) return
        isLoadingMore = true

        _bestProducts.value = Resource.Loading()

        var query: Query =
            firestore.collection("Products").orderBy("id", Query.Direction.DESCENDING)
                .limit(pageSize)

        lastVisibleDocument?.let {
            query = query.startAfter(it)
        }

        query.get().addOnSuccessListener {
            if (!it.isEmpty) {
                val newProducts = it.toObjects(Product::class.java)
                bestProductsList.addAll(newProducts)
                lastVisibleDocument = it.documents[it.size() - 1]
                viewModelScope.launch {
                    _bestProducts.value = (Resource.Success(bestProductsList.toList()))
                }
            } else {
                viewModelScope.launch {
                    _bestProducts.value = (Resource.Success(bestProductsList.toList()))
                }
            }
            isLoadingMore = false
        }.addOnFailureListener {

            _bestProducts.value = (Resource.Error(it.message.toString()))

            isLoadingMore = false
        }
    }

    private fun fetchBestDealsProducts() {
        val products =
            firestore.collection("Products").whereEqualTo("category", "Best Deals").get()
        products.addOnSuccessListener {
            val productsList = it.toObjects(Product::class.java)
            viewModelScope.launch {
                _bestDealsProducts.value = (Resource.Success(productsList))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _bestDealsProducts.value = (Resource.Error(it.message.toString()))
            }
        }
    }


    private fun fetchSpecialProducts() {
        val products =
            firestore.collection("Products").whereEqualTo("category", "Special Products").get()
        products.addOnSuccessListener {
            val productsList = it.toObjects(Product::class.java)
            viewModelScope.launch {
                _specialProducts.value = (Resource.Success(productsList))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _specialProducts.value = (Resource.Error(it.message.toString()))
            }
        }

    }


}