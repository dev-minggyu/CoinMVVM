package com.example.coinmvvm.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.NetworkState
import com.example.coinmvvm.databinding.FragmentHomeBinding
import com.example.coinmvvm.extension.showSnackBar
import com.example.coinmvvm.ui.base.BaseFragment
import com.example.coinmvvm.ui.fragment.home.adapter.CoinListPagerAdapter
import com.example.coinmvvm.ui.fragment.home.adapter.FavoriteClickListener
import com.example.coinmvvm.ui.fragment.home.adapter.TickerAdapter
import com.example.coinmvvm.ui.fragment.home.dialog.RetryDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val _homeViewModel: HomeViewModel by viewModels()

    private var tickerAdapter: TickerAdapter? = null
    private var favoriteAdapter: TickerAdapter? = null

    private var _networkSnackBar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        setupObserver()
    }

    private fun setupObserver() {
        networkStateLiveData.observe(viewLifecycleOwner) {
            when (it!!) {
                NetworkState.CONNECTED -> {
                    _networkSnackBar?.dismiss()
                    _homeViewModel.doListenPrice()
                }
                NetworkState.DISCONNECTED -> {
                    _networkSnackBar = showSnackBar("인터넷 연결을 확인해주세요.", "재시도") {
                        networkStateLiveData.updateState()
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
            viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            viewPager.adapter = CoinListPagerAdapter(tickerAdapter!!, favoriteAdapter!!)

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = (viewPager.adapter as CoinListPagerAdapter).getListTitle(position)
            }.attach()
        }
    }

    private fun initListAdapters() {
        val tickerClickListener = object : FavoriteClickListener {
            override fun onFavorite(symbol: String, isChecked: Boolean) {
                if (isChecked) {
                    _homeViewModel.addFavoriteSymbol(symbol)
                } else {
                    _homeViewModel.deleteFavoriteSymbol(symbol)
                }
            }
        }
        tickerAdapter = TickerAdapter(tickerClickListener).apply {
            tabTitle = "전체"
        }
        favoriteAdapter = TickerAdapter(tickerClickListener).apply {
            tabTitle = "즐겨찾기"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tickerAdapter = null
        favoriteAdapter = null
    }
}