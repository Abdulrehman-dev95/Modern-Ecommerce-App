package com.example.razashop.data

import android.os.Parcelable
import com.example.razashop.utils.getCurrentDate
import com.example.razashop.utils.getCurrentTime
import kotlinx.parcelize.Parcelize
import kotlin.random.Random.Default.nextLong
@Parcelize
data class Order(
    val orderStatus: String = "",
    val totalPrice: Float = 0F,
    val orderDate: String = getCurrentDate(),
    val orderTime: String = getCurrentTime(),
    val products: List<CartProduct> = emptyList(),
    val address: Address = Address(),
    val orderId: Long = nextLong(1, 100000) + totalPrice.toLong()
): Parcelable
