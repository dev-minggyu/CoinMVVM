package com.example.coinmvvm.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.NetworkState
import com.example.coinmvvm.databinding.FragmentHomeBinding
import com.example.coinmvvm.extension.getNetworkStateLiveData
import com.example.coinmvvm.extension.getViewModelFactory
import com.example.coinmvvm.extension.showSnackBar
import com.example.coinmvvm.ui.base.BaseFragment
import com.example.coinmvvm.ui.fragment.home.adapter.ListViewPagerAdapter
import com.example.coinmvvm.ui.fragment.home.adapter.TickerAdapter
import com.example.coinmvvm.ui.fragment.home.adapter.TickerClickListener
import com.example.coinmvvm.ui.fragment.home.dialog.RetryDialog
import com.google.android.material.snackbar.Snackbar

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val _homeViewModel by viewModels<HomeViewModel> { getViewModelFactory() }

    private var tickerAdapter: TickerAdapter? = null
    private var favoriteAdapter: TickerAdapter? = null

    private var _networkSnackBar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        setupObserver()
    }

    private fun setupObserver() {
        getNetworkStateLiveData().observe(viewLifecycleOwner) {
            when (it!!) {
                NetworkState.CONNECTED -> {
                    _networkSnackBar?.dismiss()
                    _homeViewModel.doListenPrice()
                }
                NetworkState.DISCONNECTED -> {
                    _networkSnackBar = showSnackBar("인터넷 연결을 확인해주세요.", "재시도") {
                        getNetworkStateLiveData().updateState()
                    }
                }
            }
        }

        _homeViewModel.tickerList.observe(viewLifecycleOwner) {
            tickerAdapter?.submitList(it)
        }

        _homeViewModel.favoriteTickerList.observe(viewLifecycleOwner) {
            favoriteAdapter?.submitList(it)
        }

        _homeViewModel.doRetry.observe(viewLifecycleOwner) {
            RetryDialog(it).show(childFragmentManager)
        }
    }

    private fun setupViewPager() {
        initListAdapters()

        dataBinding.apply {
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.offscreenPageLimit = ListViewPagerAdapter.VIEW_LIST_COUNT
            viewPager.adapter = ListViewPagerAdapter(tickerAdapter!!, favoriteAdapter!!)
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

    override fun onDestroyView() {
        super.onDestroyView()
        tickerAdapter = null
        favoriteAdapter = null
    }
}