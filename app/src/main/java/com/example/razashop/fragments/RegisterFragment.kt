package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.razashop.R
import com.example.razashop.data.User
import com.example.razashop.databinding.FragmentRegisterBinding
import com.example.razashop.utils.RegisterValidation
import com.example.razashop.utils.Resource
import com.example.razashop.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            registerButton.setOnClickListener {
                val user = User(
                    firstName = firstNameEd.text.toString().trim(),
                    lastName = lastNameEd.text.toString().trim(),
                    email = emailEd.text.toString().trim()
                )
                val password = passwordEd.text.toString()
                if (user.email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.createAccountWithEmailAndPassword(
                        user, password
                    )
                }

            }
            accountTv.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.registerButton.startAnimation()
                        }

                        is Resource.Success -> {
                            binding.registerButton.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Register Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }

                        is Resource.Error -> {
                            binding.registerButton.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Register Failed: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }

                }

            }

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect {
                    if (it.email is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.emailEd.apply {
                                requestFocus()
                                error = it.email.message
                            }
                        }
                    }
                    if (it.password is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.passwordEd.apply {
                                requestFocus()
                                error = it.password.message
                            }
                        }
                    }

                }
            }
        }


    }


}