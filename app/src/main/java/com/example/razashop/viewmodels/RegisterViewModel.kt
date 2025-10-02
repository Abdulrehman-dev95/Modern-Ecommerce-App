package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.razashop.data.User
import com.example.razashop.utils.Constants.USER_COLLECTION
import com.example.razashop.utils.RegisterFieldState
import com.example.razashop.utils.RegisterValidation
import com.example.razashop.utils.Resource
import com.example.razashop.utils.validateEmail
import com.example.razashop.utils.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register = _register.asStateFlow()

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            _register.value = Resource.Loading()
            firebaseAuth.createUserWithEmailAndPassword(
                user.email, password
            ).addOnSuccessListener {
                it.user?.let { firebaseUser ->

                    saveUserInfo(userId = firebaseUser.uid, user = user)

                }
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())

            }
        } else {
            val registerFieldState = RegisterFieldState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun saveUserInfo(userId: String, user: User) {
        db.collection(USER_COLLECTION).document(userId).set(user).addOnSuccessListener {
            _register.value = Resource.Success(user)
        }.addOnFailureListener {
            _register.value = Resource.Error(it.message.toString())
        }
    }

    fun checkValidation(user: User, password: String): Boolean {
        val validateEmail = validateEmail(user.email)
        val validatePassword = validatePassword(password)
        val shouldRegister =
            validateEmail is RegisterValidation.Success && validatePassword is RegisterValidation.Success
        return shouldRegister
    }
}