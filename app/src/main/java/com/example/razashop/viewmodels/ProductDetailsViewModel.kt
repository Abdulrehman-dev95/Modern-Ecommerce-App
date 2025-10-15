package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.razashop.data.CartProduct
import com.example.razashop.firebase.FireBaseCommon
import com.example.razashop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val fireBaseCommon: FireBaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateToCart(cartProduct: CartProduct) {
        _addToCart.value = Resource.Loading()
        firebaseFirestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
            .whereEqualTo(
                "product.id", cartProduct.product.id
            ).get().addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.let {
                    if (it.isEmpty()) { //add new product
                        addToCart(cartProduct)
                    } else {
                        val id = it.first().id
                        increaseQuantity(id, cartProduct)
                    }
                }
            }
    }


    private fun increaseQuantity(id: String, cartProduct: CartProduct) {
        fireBaseCommon.increaseQuantity(id) { _, e ->
            if (e == null) {
                _addToCart.value = Resource.Success(cartProduct)
            } else {
                _addToCart.value = Resource.Error(e.message.toString())
            }
        }
    }

    private fun addToCart(cartProduct: CartProduct) {
        fireBaseCommon.addProductIntoCart(cartProduct) { addedProduct, e ->
            if (e == null) {
                _addToCart.value = Resource.Success(addedProduct!!)
            } else {
                _addToCart.value = Resource.Error(e.message.toString())
            }
        }
    }


}