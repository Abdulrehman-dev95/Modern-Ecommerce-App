package com.example.razashop.data

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: MutableList<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
): Parcelable {
    constructor(): this(
        "0",
        "",
        "",
        0f,
        images = emptyList()
    )

}
