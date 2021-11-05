package com.example.mvvmbithumb.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.Ticker
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.repository.BithumbRepository
import com.example.mvvmbithumb.extension.asLiveData
import com.example.mvvmbithumb.util.Resource
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class HomeViewModel(private val _bithumbRepository: BithumbRepository) : ViewModel() {
    private val _tmpTickerList: MutableList<Ticker> = mutableListOf()
    private val _tickerList: MutableLiveData<MutableList<Ticker>> = MutableLiveData()
    val tickerList = _tickerList.asLiveData()

    private val _doRetry: MutableLiveData<String> = MutableLiveData()
    val doRetry = _doRetry.asLiveData()

    private suspend fun getKRWTickers() {
        when (val tickers = _bithumbRepository.getKRWTickers()) {
            is Resource.Success -> {
                val favoriteSymbolList = _bithumbRepository.getFavoriteSymbols().map {
                    it.symbol
                }
                val tickerList = tickers.data.toKRWTickerList()
                tickerList.forEach {
                    it.isFavorite = favoriteSymbolList.contains(it.symbol)
                }
                _tmpTickerList.clear()
                _tmpTickerList.addAll(tickerList)
            }
        }
    }

    fun doListenPrice() {
        viewModelScope.launch {
            if (_tmpTickerList.isEmpty()) {
                getKRWTickers()
            }

            val requestTickerData = RequestTickerData(_tmpTickerList.map { it.symbol })
            _bithumbRepository.listenTickerSocket(requestTickerData).consumeEach {
                when (it) {
                    is Resource.Success -> onReceivedTicker(it.data)
                    is Resource.Error -> onSocketError(it.message)
                }
            }
        }
    }

    fun doRetryListenPrice() {
        _bithumbRepository.stopTickerSocket()
        doListenPrice()
    }

    fun addFavoriteSymbol(symbol: String) {
        viewModelScope.launch {
            _tmpTickerList.forEach {
                if (it.symbol == symbol) {
                    it.isFavorite = true
                    return@forEach
                }
            }
            _bithumbRepository.addFavoriteTicker(symbol)
        }
    }

    fun deleteFavoriteSymbol(symbol: String) {
        viewModelScope.launch {
            _tmpTickerList.forEach {
                if (it.symbol == symbol) {
                    it.isFavorite = false
                    return@forEach
                }
            }
            _bithumbRepository.deleteFavoriteTicker(symbol)
        }
    }

    private fun onReceivedTicker(tickerData: TickerData?) {
        val tickerContent = tickerData?.ticker?.content
        tickerContent?.let { content ->
            _tickerList.value = _tmpTickerList.map { item ->
                if (item.symbol == content.symbol) {
                    item.currentPrice = content.closePrice
                    item.prevPrice = content.prevClosePrice
                }
                item
            }.toMutableList()
        }
    }

    private fun onSocketError(message: String) {
        _bithumbRepository.stopTickerSocket()
        _doRetry.value = message
    }

    override fun onCleared() {
        super.onCleared()
        _bithumbRepository.stopTickerSocket()
    }
}