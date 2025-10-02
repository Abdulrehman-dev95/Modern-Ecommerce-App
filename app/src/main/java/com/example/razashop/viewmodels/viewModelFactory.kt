package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.razashop.data.Categories
import com.google.firebase.firestore.FirebaseFirestore

class ViewModelFactory(
    private val firebaseFirestore: FirebaseFirestore,
    private val category: Categories
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firebaseFirestore, category) as T
    }
}