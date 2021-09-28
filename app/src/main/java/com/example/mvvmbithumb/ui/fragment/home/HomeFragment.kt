package com.example.mvvmbithumb.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.databinding.FragmentHomeBinding
import com.example.mvvmbithumb.repository.DataManager
import com.example.mvvmbithumb.repository.websocket.WebSocketProvider
import com.example.mvvmbithumb.ui.base.BaseFragment
import com.example.mvvmbithumb.viewmodel.ViewModelFactory

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private lateinit var _homeViewModel: HomeViewModel

    override fun onInitViewModel() {
        val factory = ViewModelFactory(DataManager(WebSocketProvider()))
        _homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _homeViewModel.subscribePrice()

        _homeViewModel.btcPrice.observe(viewLifecycleOwner, {
            _dataBinding.tvPriceBtc.text = "BTC : $it"
        })
    }
}