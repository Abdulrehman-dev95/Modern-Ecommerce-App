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
import com.example.razashop.databinding.FragmentLoginBinding
import com.example.razashop.dialogs.setUpBottomSheetDialog
import com.example.razashop.utils.Resource
import com.example.razashop.utils.moveToShoppingActivity
import com.example.razashop.viewmodels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            loginButton.setOnClickListener {
                val email = emailEd.text.toString().trim()
                val password = passwordEd.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(email, password)
                }
            }

            accountTv.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            forgetPasswordTv.setOnClickListener {
                setUpBottomSheetDialog { email ->
                    viewModel.resetPassword(email)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.login.collect {
                    when (it) {
                        is Resource.Loading -> {
                            binding.loginButton.startAnimation()
                        }

                        is Resource.Success -> {

                            binding.loginButton.revertAnimation()
                            Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT)
                                .show()
                            moveToShoppingActivity()

                        }

                        is Resource.Error -> {
                            binding.loginButton.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Login Failed: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }

            }

        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.resetPassword.collect {
                    when (it) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            Snackbar.make(
                                requireView(),
                                "Reset link was sent to your email",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        is Resource.Error -> {
                            Snackbar.make(
                                requireView(),
                                "Error: ${it.message}",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> Unit
                    }
                }

            }


        }


    }


}