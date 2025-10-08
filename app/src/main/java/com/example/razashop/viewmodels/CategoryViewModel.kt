package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.razashop.data.Categories
import com.example.razashop.data.Product
import com.example.razashop.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel(
    private val firebaseFirestore: FirebaseFirestore,
    private val category: Categories
): ViewModel() {
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    private val bestProductsList = mutableListOf<Product>()
    private var lastVisibleDocument: DocumentSnapshot? = null
    private var isLoadingMore = false
    private val pageSize = 10L

    init {
        fetchBestProducts()
        fetchOfferProducts()
    }

    fun fetchBestProducts() {
        if (isLoadingMore) return
        isLoadingMore = true

        _bestProducts.value = Resource.Loading()


        var query: Query =
            firebaseFirestore.collection("Products").whereEqualTo("category", category.category)
                .whereEqualTo("offerPercentage", null).orderBy("id", Query.Direction.DESCENDING)
                .limit(pageSize)

        lastVisibleDocument?.let {
            query = query.startAfter(it)
        }

        query.get().addOnSuccessListener {
            if (!it.isEmpty) {
                val productsList = it.toObjects(Product::class.java)
                bestProductsList.addAll(productsList)
                lastVisibleDocument = it.documents[it.size() - 1]
                _bestProducts.value = (Resource.Success(bestProductsList))
            } else {
                _bestProducts.value = (Resource.Success(bestProductsList))
            }
        }.addOnFailureListener {
            _bestProducts.value = (Resource.Error(it.message.toString()))
        }
    }

    fun fetchOfferProducts() {

        _offerProducts.value = Resource.Loading()

        firebaseFirestore.collection("Products").whereEqualTo("category", category.category)
            .whereNotEqualTo("offerPercentage", null)
            .get().addOnSuccessListener {
                val productsList = it.toObjects(Product::class.java)
                _offerProducts.value = (Resource.Success(productsList))
            }.addOnFailureListener {
                _offerProducts.value = (Resource.Error(it.message.toString()))
            }
    }


}