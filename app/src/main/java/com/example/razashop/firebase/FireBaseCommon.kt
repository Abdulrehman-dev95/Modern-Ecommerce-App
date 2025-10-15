package com.example.razashop.firebase

import com.example.razashop.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FireBaseCommon(
    firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore
) {


    private val cartCollection =
        firebaseFirestore.collection("user").document(firebaseAuth.uid!!).collection("cart")

    enum class QualityChanging {
        INCREASE, DECREASE
    }

    fun addProductIntoCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartProduct).addOnSuccessListener {
            onResult(cartProduct, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        val documentRef = cartCollection.document(documentId)
        documentRef.update("quantity", FieldValue.increment(1)).addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        val documentRef = cartCollection.document(documentId)
        documentRef.update("quantity", FieldValue.increment(-1)).addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }





}