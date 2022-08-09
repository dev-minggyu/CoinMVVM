package com.example.coinmvvm.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.NetworkState
import com.example.coinmvvm.constant.enums.SortState
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

    private var _networkSnackBar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.vm = _homeViewModel

        setupViewPager()

        setupObserver()

        setupListFilter()
    }

    private fun setupListFilter() {
        dataBinding.apply {
            layoutSort.tvSortName.setOnClickListener {
                _tickerAdapter?.submitList(null)

                layoutSort.tvSortPrice.text = getString(R.string.sort_coin_price_no)
                layoutSort.tvSortRate.text = getString(R.string.sort_coin_rate_no)
                layoutSort.tvSortVolume.text = getString(R.string.sort_coin_volume_no)

                when (layoutSort.tvSortName.text) {
                    getString(R.string.sort_coin_name_no) -> {
                        layoutSort.tvSortName.text = getString(R.string.sort_coin_name_desc)
                        _homeViewModel.sortTicker(SortState.NAME_DESC)
                    }
                    getString(R.string.sort_coin_name_desc) -> {
                        layoutSort.tvSortName.text = getString(R.string.sort_coin_name_asc)
                        _homeViewModel.sortTicker(SortState.NAME_ASC)
                    }
                    getString(R.string.sort_coin_name_asc) -> {
                        layoutSort.tvSortName.text = getString(R.string.sort_coin_name_no)
                        _homeViewModel.sortTicker(SortState.NO)
                    }
                }
            }

            layoutSort.tvSortPrice.setOnClickListener {
                _tickerAdapter?.submitList(null)

                layoutSort.tvSortName.text = getString(R.string.sort_coin_name_no)
                layoutSort.tvSortRate.text = getString(R.string.sort_coin_rate_no)
                layoutSort.tvSortVolume.text = getString(R.string.sort_coin_volume_no)

                when (layoutSort.tvSortPrice.text) {
                    getString(R.string.sort_coin_price_no) -> {
                        layoutSort.tvSortPrice.text = getString(R.string.sort_coin_price_desc)
                        _homeViewModel.sortTicker(SortState.PRICE_DESC)
                    }
                    getString(R.string.sort_coin_price_desc) -> {
                        layoutSort.tvSortPrice.text = getString(R.string.sort_coin_price_asc)
                        _homeViewModel.sortTicker(SortState.PRICE_ASC)
                    }
                    getString(R.string.sort_coin_price_asc) -> {
                        layoutSort.tvSortPrice.text = getString(R.string.sort_coin_price_no)
                        _homeViewModel.sortTicker(SortState.NO)
                    }
                }
            }

            layoutSort.tvSortRate.setOnClickListener {
                _tickerAdapter?.submitList(null)

                layoutSort.tvSortName.text = getString(R.string.sort_coin_name_no)
                layoutSort.tvSortPrice.text = getString(R.string.sort_coin_price_no)
                layoutSort.tvSortVolume.text = getString(R.string.sort_coin_volume_no)

                when (layoutSort.tvSortRate.text) {
                    getString(R.string.sort_coin_rate_no) -> {
                        layoutSort.tvSortRate.text = getString(R.string.sort_coin_rate_desc)
                        _homeViewModel.sortTicker(SortState.RATE_DESC)
                    }
                    getString(R.string.sort_coin_rate_desc) -> {
                        layoutSort.tvSortRate.text = getString(R.string.sort_coin_rate_asc)
                        _homeViewModel.sortTicker(SortState.RATE_ASC)
                    }
                    getString(R.string.sort_coin_rate_asc) -> {
                        layoutSort.tvSortRate.text = getString(R.string.sort_coin_rate_no)
                        _homeViewModel.sortTicker(SortState.NO)
                    }
                }
            }

            layoutSort.tvSortVolume.setOnClickListener {
                _tickerAdapter?.submitList(null)

                layoutSort.tvSortName.text = getString(R.string.sort_coin_name_no)
                layoutSort.tvSortPrice.text = getString(R.string.sort_coin_price_no)
                layoutSort.tvSortRate.text = getString(R.string.sort_coin_rate_no)

                when (layoutSort.tvSortVolume.text) {
                    getString(R.string.sort_coin_volume_no) -> {
                        layoutSort.tvSortVolume.text = getString(R.string.sort_coin_volume_desc)
                        _homeViewModel.sortTicker(SortState.VOLUME_DESC)
                    }
                    getString(R.string.sort_coin_volume_desc) -> {
                        layoutSort.tvSortVolume.text = getString(R.string.sort_coin_volume_asc)
                        _homeViewModel.sortTicker(SortState.VOLUME_ASC)
                    }
                    getString(R.string.sort_coin_volume_asc) -> {
                        layoutSort.tvSortVolume.text = getString(R.string.sort_coin_volume_no)
                        _homeViewModel.sortTicker(SortState.NO)
                    }
                }
            }
        }
    }

    private fun setupObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> _homeViewModel.listenPrice()
                    Lifecycle.Event.ON_STOP -> _homeViewModel.unlistenPrice()
                    else -> {}
                }
            }
        })

        networkStateLiveData.observe(viewLifecycleOwner) {
            when (it!!) {
                NetworkState.CONNECTED -> {
                    _networkSnackBar?.dismiss()
                    _homeViewModel.listenPrice()
                }
                NetworkState.DISCONNECTED -> {
                    _networkSnackBar = showSnackBar(
                        getString(R.string.snackbar_check_internet_connection),
                        getString(R.string.snackbar_retry)
                    ) {
                        networkStateLiveData.updateState()
                    }
                }
            }
        }

        _homeViewModel.tickerList.observe(viewLifecycleOwner) {
            _tickerAdapter?.submitList(it)
            _favoriteAdapter?.submitList(
                it?.filter { ticker ->
                    ticker.isFavorite
                }
            )
        }

        _homeViewModel.socketError.observe(viewLifecycleOwner) {
            _networkSnackBar = showSnackBar(
                it,
                getString(R.string.snackbar_retry)
            ) {
                networkStateLiveData.updateState()
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
        _networkSnackBar = null
    }
}