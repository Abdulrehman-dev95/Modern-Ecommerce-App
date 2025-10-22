package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.razashop.R
import com.example.razashop.data.Address
import com.example.razashop.databinding.FargmentAddressBinding
import com.example.razashop.utils.Resource
import com.example.razashop.viewmodels.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment : Fragment(R.layout.fargment_address) {
    private lateinit var binding: FargmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FargmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val name = edFullName.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val address = edStreet.text.toString()
                val province = edProvince.text.toString()

                val addressObj = Address(
                    addressTitle = addressTitle,
                    fullName = name,
                    phone = phone,
                    city = city,
                    address = address,
                    province = province
                )
                viewModel.addAddress(addressObj)
            }

            buttonDelete.setOnClickListener {

            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading<*> -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Error<*> -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Success<*> -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Address added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()
                    }

                    else -> Unit
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()

            }


        }

    }
}