package com.example.razashop.utils

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty())
        return RegisterValidation.Failed(message = "Email cannot be empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed(message = "Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty()) {
        return RegisterValidation.Failed(message = "Password cannot be empty")
    }
    if (
        password.length < 6
    ) {
        return RegisterValidation.Failed(message = "Password should contains 6 char")
    }
    return RegisterValidation.Success
}