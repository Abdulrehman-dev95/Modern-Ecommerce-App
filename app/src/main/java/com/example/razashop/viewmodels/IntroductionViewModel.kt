package com.example.razashop.viewmodels

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.razashop.R
import com.example.razashop.utils.Constants.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class IntroductionViewModel @javax.inject.Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) :
    ViewModel() {
    private val _navigation = MutableStateFlow(0)
    val navigation = _navigation.asStateFlow()


    companion object {
        const val SHOPPING_ACTIVITY = 15
        val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_fragmentIntroduction_to_accountOptionsFragment
    }

    init {
        val isStartButtonClick = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser
        if (user != null) {
            _navigation.value = SHOPPING_ACTIVITY
        } else if (
            isStartButtonClick
        ) {
            _navigation.value = ACCOUNT_OPTIONS_FRAGMENT
        }
    }

    fun buttonClicked() = sharedPreferences.edit {
        putBoolean(INTRODUCTION_KEY, true).apply()
    }

}