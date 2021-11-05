package com.example.mvvmbithumb.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.mvvmbithumb.R
import com.example.mvvmbithumb.constant.enums.NetworkState
import com.example.mvvmbithumb.databinding.FragmentHomeBinding
import com.example.mvvmbithumb.extension.getNetworkStateLiveData
import com.example.mvvmbithumb.extension.getViewModelFactory
import com.example.mvvmbithumb.extension.showSnackBar
import com.example.mvvmbithumb.ui.base.BaseFragment
import com.example.mvvmbithumb.ui.fragment.home.adapter.ListViewPagerAdapter
import com.example.mvvmbithumb.ui.fragment.home.adapter.TickerAdapter
import com.example.mvvmbithumb.ui.fragment.home.adapter.TickerClickListener
import com.example.mvvmbithumb.ui.fragment.home.dialog.RetryDialog

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val _homeViewModel by viewModels<HomeViewModel> { getViewModelFactory() }

    private lateinit var tickerAdapter: TickerAdapter
    private lateinit var favoriteAdapter: TickerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

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

        _homeViewModel.favoriteTickerList.observe(viewLifecycleOwner, {
            favoriteAdapter.submitList(it)
        })

        _homeViewModel.doRetry.observe(viewLifecycleOwner, {
            RetryDialog(it).show(childFragmentManager)
        })
    }

    private fun setupViewPager() {
        initListAdapters()

        _dataBinding.apply {
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.offscreenPageLimit = ListViewPagerAdapter.VIEW_LIST_COUNT
            viewPager.adapter = ListViewPagerAdapter(tickerAdapter, favoriteAdapter)
        }
    }

    private fun initListAdapters() {
        val tickerClickListener = object : TickerClickListener {
            override fun onFavorite(symbol: String, isChecked: Boolean) {
                if (isChecked) {
                    _homeViewModel.addFavoriteSymbol(symbol)
                } else {
                    _homeViewModel.deleteFavoriteSymbol(symbol)
                }
            }
        }
        tickerAdapter = TickerAdapter(tickerClickListener)
        favoriteAdapter = TickerAdapter(tickerClickListener)
    }
}