package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razashop.data.Address
import com.example.razashop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) :
    ViewModel() {
    private val _address = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validate = validateInputs(address)
        if (validate) {
            _address.value = Resource.Loading()
            firebaseFirestore.collection("user").document(auth.uid!!).collection("address")
                .document().set(address).addOnSuccessListener {
                    _address.value = Resource.Success(address)
                }.addOnFailureListener {
                    _address.value = Resource.Error(it.message.toString())
                }
        } else {
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
        }

    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.address.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.province.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty()
    }

}