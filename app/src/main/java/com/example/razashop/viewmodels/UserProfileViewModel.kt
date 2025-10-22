package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.razashop.data.User
import com.example.razashop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
): ViewModel() {
    private val _userProfile = MutableStateFlow<Resource<User>>(Resource.Unspecified())
   val userProfile = _userProfile.asStateFlow()

    init {
        getUser()
    }


    fun getUser() {
        firebaseFirestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _userProfile.value = Resource.Error(error.message.toString())
                } else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        _userProfile.value = Resource.Success(user)
                    }
                }
            }
    }

    fun logOut() {
        auth.signOut()
    }


}