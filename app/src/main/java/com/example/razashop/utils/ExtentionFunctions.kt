package com.example.razashop.utils

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.example.razashop.R
import com.example.razashop.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

fun Fragment.moveToShoppingActivity() {
    val intent =
        Intent(requireActivity(), ShoppingActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        )
    startActivity(intent)
}

fun Fragment.hideBottomNavigationView() {
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationView.visibility = View.INVISIBLE

}

fun Fragment.showBottomNavigationView() {
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )
    bottomNavigationView.visibility = View.VISIBLE

}

fun Float.priceAfterDiscount(price: Float): String {
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price
    return String.format(locale = Locale.getDefault(), "%.2f", priceAfterOffer)
}


