package com.example.razashop.utils

import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.razashop.activities.ShoppingActivity

fun Fragment.moveToShoppingActivity() {
    val intent =
        Intent(requireActivity(), ShoppingActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        )
    startActivity(intent)
}