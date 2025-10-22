package com.example.razashop.viewmodels

import androidx.lifecycle.ViewModel
import com.example.razashop.data.Address
import com.example.razashop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _getAddress = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val getAddress = _getAddress.asStateFlow()

    init {
        fetchAddresses()
    }

    fun fetchAddresses() {
        _getAddress.value = Resource.Loading()

        firebaseFirestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { addresses, error ->
                if (error == null) {
                    val listOfAddress = addresses?.toObjects(Address::class.java)

                    _getAddress.value = Resource.Success(listOfAddress ?: emptyList())
                } else {
                    _getAddress.value = Resource.Error(error.message.toString())
                }

            }
    }

}