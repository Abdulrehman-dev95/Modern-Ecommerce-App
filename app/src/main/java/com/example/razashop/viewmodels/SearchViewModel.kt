package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razashop.data.Product
import com.example.razashop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val firebaseFirestore: FirebaseFirestore
) : ViewModel() {
    private val _searchResults = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchResults = _searchResults.asStateFlow()

    private var searchJob: Job? = null

    fun searchProducts(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val formattedQuery = query.trim()
            if (formattedQuery.isNotEmpty()) {
                delay(500L)
                fetchFromFirestore(formattedQuery)

            } else {
                _searchResults.value = Resource.Unspecified()
            }
        }

    }

    private fun fetchFromFirestore(formattedQuery: String) {
        _searchResults.value = Resource.Loading()
        firebaseFirestore.collection("Products").orderBy("name").startAt(formattedQuery)
            .endAt(formattedQuery + "\uf8ff").get().addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                _searchResults.value = Resource.Success(products)

            }.addOnFailureListener {
                _searchResults.value = Resource.Error("Sorry! Result not found")
            }
    }


}