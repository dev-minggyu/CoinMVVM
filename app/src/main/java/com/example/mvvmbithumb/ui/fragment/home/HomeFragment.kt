package com.example.mvvmbithumb.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.databinding.FragmentHomeBinding
import com.example.mvvmbithumb.extension.getViewModelFactory
import com.example.mvvmbithumb.ui.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val _homeViewModel by viewModels<HomeViewModel> { getViewModelFactory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _homeViewModel.subscribePrice()

        _homeViewModel.btcPrice.observe(viewLifecycleOwner, {
            _dataBinding.tvPriceBtc.text = "BTC : $it"
        })
    }
}