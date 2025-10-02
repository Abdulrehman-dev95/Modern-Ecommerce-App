package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.razashop.R
import com.example.razashop.databinding.FragmentIntroductionBinding
import com.example.razashop.utils.moveToShoppingActivity
import com.example.razashop.viewmodels.IntroductionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentIntroduction() : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startButton.setOnClickListener {
            viewModel.buttonClicked()
            findNavController().navigate(R.id.action_fragmentIntroduction_to_accountOptionsFragment)
        }

        val navValue = viewModel.navigation.value
        when (navValue) {
            IntroductionViewModel.SHOPPING_ACTIVITY -> {
                moveToShoppingActivity()
            }

            IntroductionViewModel.ACCOUNT_OPTIONS_FRAGMENT -> {
                findNavController().navigate(R.id.action_fragmentIntroduction_to_accountOptionsFragment)
            }

        }

    }

}