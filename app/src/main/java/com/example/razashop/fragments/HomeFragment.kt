package com.example.razashop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.razashop.R
import com.example.razashop.adapters.HomeViewPagerAdapter
import com.example.razashop.databinding.FragmentHomeBinding
import com.example.razashop.fragments.categories.AccessoryFragment
import com.example.razashop.fragments.categories.ChairFragment
import com.example.razashop.fragments.categories.CupBoardFragment
import com.example.razashop.fragments.categories.FurnitureFragment
import com.example.razashop.fragments.categories.MainCategoryFragment
import com.example.razashop.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf(
            MainCategoryFragment(),
            ChairFragment(),
            CupBoardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )
        val viewPager2Adapter =
            HomeViewPagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPager2Adapter
        TabLayoutMediator(
            binding.tabBar, binding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()
    }


}