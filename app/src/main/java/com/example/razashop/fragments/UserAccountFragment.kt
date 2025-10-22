package com.example.razashop.fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.razashop.R
import com.example.razashop.data.User
import com.example.razashop.databinding.FragmentUserAccountBinding
import com.example.razashop.dialogs.setUpBottomSheetDialog
import com.example.razashop.utils.Resource
import com.example.razashop.viewmodels.LoginViewModel
import com.example.razashop.viewmodels.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment : Fragment(R.layout.fragment_user_account) {
    lateinit var binding: FragmentUserAccountBinding
    private val viewModel: UserAccountViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private var imageUri: Uri? = null
    private lateinit var galleryLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getUser.collectLatest {
                    when (it) {
                        is Resource.Error<*> -> {
                            binding.progressbarAccount.visibility = View.GONE
                            binding.imageUser.visibility = View.VISIBLE
                            binding.edEmail.visibility = View.VISIBLE
                            binding.buttonSave.visibility = View.VISIBLE
                            binding.edFirstName.visibility = View.VISIBLE
                            binding.edLastName.visibility = View.VISIBLE
                            binding.tvUpdatePassword.visibility = View.VISIBLE
                            binding.imageEdit.visibility = View.VISIBLE
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is Resource.Loading<*> -> {
                            binding.progressbarAccount.visibility = View.VISIBLE
                            binding.imageUser.visibility = View.INVISIBLE
                            binding.edEmail.visibility = View.INVISIBLE
                            binding.buttonSave.visibility = View.INVISIBLE
                            binding.edFirstName.visibility = View.INVISIBLE
                            binding.edLastName.visibility = View.INVISIBLE
                            binding.tvUpdatePassword.visibility = View.INVISIBLE
                            binding.imageEdit.visibility = View.INVISIBLE

                        }

                        is Resource.Success<*> -> {
                            binding.progressbarAccount.visibility = View.GONE
                            binding.imageUser.visibility = View.VISIBLE
                            binding.edEmail.visibility = View.VISIBLE
                            binding.buttonSave.visibility = View.VISIBLE
                            binding.edFirstName.visibility = View.VISIBLE
                            binding.edLastName.visibility = View.VISIBLE
                            binding.tvUpdatePassword.visibility = View.VISIBLE
                            binding.imageEdit.visibility = View.VISIBLE

                            Glide.with(this@UserAccountFragment).load(it.data?.imagePath).error(
                                Color.BLACK.toDrawable()
                            )
                                .into(binding.imageUser)
                            binding.edEmail.setText(it.data?.email)
                            binding.edFirstName.setText(it.data?.firstName)
                            binding.edLastName.setText(it.data?.lastName)

                        }

                        is Resource.Unspecified<*> -> {}
                    }


                }


            }

        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editInfo.collectLatest {
                    when (it) {
                        is Resource.Error<*> -> {
                            binding.buttonSave.revertAnimation()
                            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }

                        is Resource.Loading<*> -> {
                            binding.buttonSave.startAnimation()
                            findNavController().navigateUp()
                        }

                        is Resource.Success<*> -> {
                            binding.buttonSave.revertAnimation()
                            findNavController().navigateUp()
                        }

                        is Resource.Unspecified<*> -> {}
                    }


                }


            }

        }

        binding.buttonSave.setOnClickListener {
            val firstName = binding.edFirstName.text.toString().trim()
            val lastName = binding.edLastName.text.toString().trim()
            val email = binding.edEmail.text.toString().trim()
            val user = User(firstName, lastName, email)
            viewModel.editInfo(user = user, imageUri = imageUri)

        }

        binding.imageEdit.setOnClickListener {
            galleryLauncher = registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) {
                imageUri = it
                Glide.with(this@UserAccountFragment).load(it).into(binding.imageUser)
            }
            galleryLauncher.launch("image/*")

        }

        binding.tvUpdatePassword.setOnClickListener {
            setUpBottomSheetDialog {
                loginViewModel.resetPassword(it)
            }

        }


    }

}