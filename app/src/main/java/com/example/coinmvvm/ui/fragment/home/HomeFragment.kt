package com.example.coinmvvm.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.coinmvvm.R
import com.example.coinmvvm.databinding.FragmentHomeBinding
import com.example.coinmvvm.extension.showSnackBar
import com.example.coinmvvm.ui.base.BaseFragment
import com.example.coinmvvm.ui.fragment.home.adapter.CoinListPagerAdapter
import com.example.coinmvvm.ui.fragment.home.adapter.FavoriteClickListener
import com.example.coinmvvm.ui.fragment.home.adapter.TickerAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val _homeViewModel: HomeViewModel by viewModels()

    private var _tickerAdapter: TickerAdapter? = null
    private var _favoriteAdapter: TickerAdapter? = null

    private var _errorSnackBar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.vm = _homeViewModel

        setupViewPager()

        setupObserver()
    }

    private fun setupObserver() {
        _homeViewModel.tickerList.observe(viewLifecycleOwner) {
            _errorSnackBar?.dismiss()
            _tickerAdapter?.submitList(it)
            _favoriteAdapter?.submitList(
                it?.filter { ticker ->
                    ticker.isFavorite
                }
            )
        }

        _homeViewModel.socketError.observe(viewLifecycleOwner) {
            _errorSnackBar = showSnackBar(
                it,
                getString(R.string.snackbar_retry)
            ) {
                _homeViewModel.retryListenPrice()
            }
        }
    }

    private fun setupViewPager() {
        initListAdapters()

        dataBinding.apply {
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
            viewPager.adapter = CoinListPagerAdapter(_tickerAdapter!!, _favoriteAdapter!!)

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = (viewPager.adapter as CoinListPagerAdapter).getListTitle(position)
            }.attach()
        }
    }

    private fun initListAdapters() {
        val favoriteClickListener = object : FavoriteClickListener {
            override fun onAddFavorite(symbol: String) {
                _homeViewModel.addFavoriteSymbol(symbol)
            }

            override fun onDeleteFavorite(symbol: String) {
                _homeViewModel.deleteFavoriteSymbol(symbol)
            }
        }
        _tickerAdapter = TickerAdapter(favoriteClickListener)
        _favoriteAdapter = TickerAdapter(favoriteClickListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _tickerAdapter = null
        _favoriteAdapter = null
    }
}