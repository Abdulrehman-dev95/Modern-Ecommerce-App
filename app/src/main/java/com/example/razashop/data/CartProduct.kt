package com.example.razashop.data

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
): Parcelable {
    constructor(): this(Product(), 1, null, null)
}

