package com.example.razashop.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.ui.setupWithNavController
import com.example.razashop.R
import com.example.razashop.databinding.ActivityShoppingBinding
import com.example.razashop.utils.Resource
import com.example.razashop.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.shoppingHostFragment) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.cartProducts.collect {
                    when (it) {
                        is Resource.Success -> {
                            val count = it.data?.size ?: 0
                            val bottomNavigation = binding.bottomNavigation
                            bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                                number = count
                                backgroundColor =
                                    ContextCompat.getColor(this@ShoppingActivity, R.color.g_blue)
                            }
                        }

                        else -> Unit
                    }
                }

            }
        }

    }
}