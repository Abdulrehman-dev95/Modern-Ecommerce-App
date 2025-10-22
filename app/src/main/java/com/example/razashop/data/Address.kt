package com.example.razashop.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val address: String,
    val city: String,
    val phone: String,
    val province: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}
