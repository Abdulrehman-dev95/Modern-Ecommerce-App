package com.example.razashop.data

sealed class Categories(
    val category: String
) {
    object Chair : Categories("Chair")
    object Cupboard : Categories("Cupboard")
    object Accessory : Categories("Accessory")
    object Table : Categories("Table")
    object Furniture : Categories("Furniture")
}

