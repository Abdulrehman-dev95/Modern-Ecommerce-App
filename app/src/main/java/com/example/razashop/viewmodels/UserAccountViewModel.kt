package com.example.razashop.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razashop.data.SupaBaseStorageClient
import com.example.razashop.data.User
import com.example.razashop.utils.RegisterValidation
import com.example.razashop.utils.Resource
import com.example.razashop.utils.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val supaBaseStorageClient: SupaBaseStorageClient,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _getUser = MutableStateFlow<Resource<User>>(
        Resource.Unspecified()
    )


    val getUser = _getUser.asStateFlow()

    private val _editInfo = MutableStateFlow<Resource<User>>(
        Resource.Unspecified()
    )

    val editInfo = _editInfo.asStateFlow()

    init {
        getUser()
    }


    fun getUser() {
        _getUser.value = Resource.Loading()
        firebaseFirestore.collection("user").document(auth.uid!!).get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            user?.let { user ->
                _getUser.value = Resource.Success(user)
            }
        }.addOnFailureListener {
            _getUser.value = Resource.Error(it.message.toString())
        }
    }

    fun editInfo(user: User, imageUri: Uri?) {
        val areInputsValid =
            validateEmail(user.email) is RegisterValidation.Success && user.firstName.trim()
                .isNotEmpty() && user.lastName.trim().isNotEmpty()

        if (areInputsValid) {
            _editInfo.value = Resource.Error("Check your inputs")
            return

        } else {
            _editInfo.value = Resource.Loading()

        }
        if (imageUri == null) {
            saveUserInfo(user, true)
        } else {
            saveUserInfoWithNewImage(user, imageUri)
        }

    }

    private fun saveUserInfoWithNewImage(
        user: User,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            try {
                val imagePath = supaBaseStorageClient.uploadImage(uri = imageUri)
                imagePath?.let {
                    val newUser = user.copy(imagePath = imagePath)
                    saveUserInfo(newUser, false)
                }
            } catch (e: Exception) {
                _editInfo.value = Resource.Error(e.message.toString())
            }
        }
    }

    private fun saveUserInfo(user: User, isGetOldImage: Boolean) {
        firebaseFirestore.runTransaction {
            val documentRef = firebaseFirestore.collection("user").document(auth.uid!!)
            if (isGetOldImage) {
                val currentUser = it.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                it.set(documentRef, newUser)
            } else {
                it.set(documentRef, user)
            }
        }.addOnSuccessListener {
            _editInfo.value = Resource.Success(user)
        }.addOnFailureListener {
            _editInfo.value = Resource.Error(it.message.toString())
        }
    }
}