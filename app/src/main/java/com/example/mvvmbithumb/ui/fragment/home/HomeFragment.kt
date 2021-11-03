package com.example.mvvmbithumb.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.constant.enums.NetworkState
import com.example.mvvmbithumb.databinding.FragmentHomeBinding
import com.example.mvvmbithumb.extension.getNetworkStateLiveData
import com.example.mvvmbithumb.extension.getViewModelFactory
import com.example.mvvmbithumb.extension.showSnackBar
import com.example.mvvmbithumb.ui.base.BaseFragment
import com.example.mvvmbithumb.ui.fragment.home.adapter.TickerAdapter
import com.example.mvvmbithumb.ui.fragment.home.adapter.TickerClickListener
import com.example.mvvmbithumb.ui.fragment.home.dialog.RetryDialog

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val _homeViewModel by viewModels<HomeViewModel> { getViewModelFactory() }

    private lateinit var tickerAdapter: TickerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTickerListAdapter()

        setupObserver()
    }

    private fun setupObserver() {
        getNetworkStateLiveData().observe(viewLifecycleOwner, {
            when (it!!) {
                NetworkState.CONNECTED -> {
                    _homeViewModel.doListenPrice()
                }
                NetworkState.DISCONNECTED -> {
                    showSnackBar("인터넷 연결을 확인해주세요.", "재시도") {
                        _homeViewModel.doRetryListenPrice()
                    }
                }
            }
        })

        _homeViewModel.tickerList.observe(viewLifecycleOwner, {
            tickerAdapter.submitList(it)
        })

        _homeViewModel.doRetry.observe(viewLifecycleOwner, {
            RetryDialog(it).show(childFragmentManager)
        })
    }

    private fun setupTickerListAdapter() {
        _dataBinding.tickerList.itemAnimator = null
        tickerAdapter = TickerAdapter(object : TickerClickListener {
            override fun onFavorite(symbol: String, isChecked: Boolean) {
                if (isChecked) {
                    _homeViewModel.addFavoriteSymbol(symbol)
                } else {
                    _homeViewModel.deleteFavoriteSymbol(symbol)
                }
            }
        })
        _dataBinding.tickerList.adapter = tickerAdapter
    }
}