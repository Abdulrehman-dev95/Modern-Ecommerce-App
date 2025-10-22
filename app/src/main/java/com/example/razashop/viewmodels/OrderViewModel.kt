package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.razashop.data.Order
import com.example.razashop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class OrderViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(order: Order) {
        _order.value = Resource.Loading()
        firebaseFirestore.runBatch {

            firebaseFirestore.collection("user").document(auth.uid!!).collection("orders")
                .document().set(order)
            firebaseFirestore.collection("orders").document().set(order)

            firebaseFirestore.collection("user").document(auth.uid!!).collection("cart").get()
                .addOnSuccessListener {
                    it.documents.forEach { documentSnapshot ->
                        documentSnapshot.reference.delete()
                    }
                }

        }.addOnSuccessListener {
            _order.value = Resource.Success(order)

        }.addOnFailureListener {
            _order.value = Resource.Error(it.message.toString())
        }
    }


}