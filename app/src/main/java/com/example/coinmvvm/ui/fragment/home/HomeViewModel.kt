package com.example.coinmvvm.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinmvvm.App.Companion.getString
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.SortState
import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.extension.asLiveData
import com.example.coinmvvm.extension.asSingleLiveData
import com.example.coinmvvm.util.Resource
import com.example.coinmvvm.util.livedata.MutableSingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val _coinRepository: CoinRepository
) : ViewModel() {
    private val _tmpTickerList: MutableList<Ticker> = mutableListOf()

    private val _tickerList: MutableLiveData<List<Ticker>?> = MutableLiveData()
    val tickerList = _tickerList.asLiveData()

    private var _sortState = SortState.NO

    private var _filterTickerSymbol: String = ""

    private val _socketError: MutableSingleLiveData<String> = MutableSingleLiveData()
    val socketError = _socketError.asSingleLiveData()

    private var _isSocketClose = true

    val filterTickerSymbol = { text: String ->
        _filterTickerSymbol = text
        notifySortedTickerList()
    }

    private suspend fun getKRWTickers(): Boolean {
        return when (val tickerList = _coinRepository.getKRWTickers()) {
            is Resource.Success -> {
                _tmpTickerList.clear()
                _tmpTickerList.addAll(tickerList.data)
                sortTicker()
                _tickerList.value = _tmpTickerList.toList()
                true
            }
            else -> false
        }
    }

    fun listenPrice() {
        if (_isSocketClose) {
            _isSocketClose = false
            viewModelScope.launch {
                if (!getKRWTickers()) {
                    _isSocketClose = true
                    onError(getString(R.string.error_getting_coin_list))
                    return@launch
                }

                val requestTickerData = RequestTickerData(_tmpTickerList.map { it.getSymbolName() })
                _coinRepository.tickerSocket(requestTickerData).collect {
                    when (it) {
                        is Resource.Success -> onReceivedTicker(it.data)
                        is Resource.Error -> {
                            _isSocketClose = true
                            onError(it.message ?: getString(R.string.error_getting_coin_list))
                        }
                    }
                }
            }
        }
    }

    fun unlistenPrice() {
        viewModelScope.launch {
            _coinRepository.stopTickerSocket()
        }
    }

    fun retryListenPrice() {
        unlistenPrice()
        listenPrice()
    }

    fun addFavoriteSymbol(symbol: String) {
        viewModelScope.launch {
            val index = _coinRepository.addFavoriteTicker(symbol)
            _tmpTickerList.find {
                it.getSymbolName() == symbol
            }?.apply {
                isFavorite = true
                favoriteIndex = index
            }
            notifySortedTickerList()
        }
    }

    fun deleteFavoriteSymbol(symbol: String) {
        viewModelScope.launch {
            _coinRepository.deleteFavoriteTicker(symbol)
            _tmpTickerList.find {
                it.getSymbolName() == symbol
            }?.apply {
                isFavorite = false
            }
            notifySortedTickerList()
        }
    }

    fun sortTicker(sortState: SortState) {
        _sortState = sortState
        notifySortedTickerList()
    }

    private fun sortTicker() {
        when (_sortState) {
            SortState.NO -> _tmpTickerList.sortBy { it.index }

            SortState.NAME_DESC -> _tmpTickerList.sortByDescending { it.symbol }
            SortState.NAME_ASC -> _tmpTickerList.sortBy { it.symbol }

            SortState.PRICE_DESC -> _tmpTickerList.sortByDescending { it.currentPrice.toFloat() }
            SortState.PRICE_ASC -> _tmpTickerList.sortBy { it.currentPrice.toFloat() }

            SortState.RATE_DESC -> _tmpTickerList.sortByDescending { it.getRateOfChange() }
            SortState.RATE_ASC -> _tmpTickerList.sortBy { it.getRateOfChange() }

            SortState.VOLUME_DESC -> _tmpTickerList.sortByDescending { it.transactionAmount.toFloat() }
            SortState.VOLUME_ASC -> _tmpTickerList.sortBy { it.transactionAmount.toFloat() }
        }
    }

    private fun notifySortedTickerList() {
        sortTicker()
        _tickerList.value = filterTicker()
    }

    private fun filterTicker(): List<Ticker> {
        return if (_filterTickerSymbol.isNotEmpty()) {
            _tmpTickerList.filter { it.symbol.startsWith(_filterTickerSymbol) }
        } else {
            _tmpTickerList
        }
    }

    private suspend fun onReceivedTicker(tickerData: TickerData?) {
        val tickerContent = tickerData?.ticker?.content
        tickerContent?.let { content ->
            _tmpTickerList.find {
                it.getSymbolName() == content.symbol
            }?.apply {
                currentPrice = content.closePrice
                prevPrice = content.prevClosePrice
            }
            notifySortedTickerList()
        }
    }

    private fun onError(message: String) {
        _socketError.setValue(message)
    }

    override fun onCleared() {
        super.onCleared()
        unlistenPrice()
    }
}
