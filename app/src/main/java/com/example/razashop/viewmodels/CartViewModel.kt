package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razashop.data.CartProduct
import com.example.razashop.firebase.FireBaseCommon
import com.example.razashop.utils.Resource
import com.example.razashop.utils.afterDiscount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val fireBaseCommon: FireBaseCommon,
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var documents = emptyList<DocumentSnapshot>()

    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }

            else -> null
        }
    }


    init {
        getCartProducts()
    }


    fun getCartProducts() {
        _cartProducts.value = Resource.Loading()
        firebaseFirestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { list, error ->
                if (error != null || list == null) {
                    _cartProducts.value = Resource.Error(error?.message.toString())
                } else {
                    _cartProducts.value = Resource.Success(list.toObjects(CartProduct::class.java))
                    documents = list.documents
                }

            }
    }

    fun changeQuantity(cartProduct: CartProduct, qualityChanging: FireBaseCommon.QualityChanging) {

        val index = cartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val id = documents[index].id

            when (qualityChanging) {
                FireBaseCommon.QualityChanging.INCREASE -> {
                    fireBaseCommon.increaseQuantity(id) { _, exception ->
                        if (exception != null) {

                            _cartProducts.value = Resource.Error(exception.toString())
                        }

                    }
                }

                FireBaseCommon.QualityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch {
                            _deleteDialog.emit(cartProduct)
                        }
                        return
                    }
                    fireBaseCommon.decreaseQuantity(id) { _, exception ->
                        if (exception != null) {
                            _cartProducts.value = Resource.Error(exception.toString())
                        }

                    }
                }
            }
        }
    }
    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = documents[index].id
            firebaseFirestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete().addOnSuccessListener {

                }.addOnFailureListener {
                    _cartProducts.value = Resource.Error(it.toString())
                }
        }
    }

}


private fun calculatePrice(data: List<CartProduct>): Float {
    return data.sumOf {
        if (it.product.offerPercentage != null) {
            (it.product.price.afterDiscount(it.product.offerPercentage)
                .toFloat() * it.quantity).toDouble()
        } else {
            (it.product.price * it.quantity).toDouble()
        }
    }.toFloat()
}



