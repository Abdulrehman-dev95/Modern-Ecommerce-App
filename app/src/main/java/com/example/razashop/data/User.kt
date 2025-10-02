package com.example.razashop.data

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath: String = ""
) {
    constructor() : this("", "", "", "")
}
