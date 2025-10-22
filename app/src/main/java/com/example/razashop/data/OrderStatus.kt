package com.example.razashop.data

sealed class OrderStatus (val status: String){
    object Ordered: OrderStatus("Ordered")
    object Canceled: OrderStatus("Canceled")
    object Confirmed: OrderStatus("Confirmed")
    object Shipped: OrderStatus("Shipped")
    object Delivered: OrderStatus("Delivered")
    object Returned: OrderStatus("Returned")

    companion object {
        fun fromStatus(status: String): OrderStatus {
            return when (status) {
                "Ordered" -> Ordered
                "Canceled" -> Canceled
                "Confirmed" -> Confirmed
                "Shipped" -> Shipped
                "Delivered" -> Delivered
                "Returned" -> Returned
                else -> Ordered
            }
        }
    }
}


