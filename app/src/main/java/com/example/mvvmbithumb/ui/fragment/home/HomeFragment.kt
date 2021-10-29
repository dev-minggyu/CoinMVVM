package com.example.mvvmbithumb.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.constant.enums.NetworkState
import com.example.mvvmbithumb.databinding.FragmentHomeBinding
import com.example.mvvmbithumb.extension.getNetworkStateLiveData
import com.example.mvvmbithumb.extension.getViewModelFactory
import com.example.mvvmbithumb.extension.showToast
import com.example.mvvmbithumb.ui.base.BaseFragment
import com.example.mvvmbithumb.ui.fragment.home.adapter.TickerAdapter

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val _homeViewModel by viewModels<HomeViewModel> { getViewModelFactory() }

    private lateinit var tickerAdapter: TickerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListAdapter()

        setupObserver()
    }

    private fun setupObserver() {
        getNetworkStateLiveData().observe(viewLifecycleOwner, {
            when (it!!) {
                NetworkState.CONNECTED -> {
                    _homeViewModel.subscribePrice()
                }
                NetworkState.DISCONNECTED -> {
                    showToast("인터넷 연결을 확인해주세요.")
                }
            }
        })

        _homeViewModel.tickerList.observe(viewLifecycleOwner, {
            tickerAdapter.submitList(it)
        })
    }

    private fun setupListAdapter() {
        _dataBinding.tickerList.itemAnimator = null
        tickerAdapter = TickerAdapter()
        _dataBinding.tickerList.adapter = tickerAdapter
    }
}