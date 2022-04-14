package com.example.coinmvvm.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.extension.asLiveData
import com.example.coinmvvm.util.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val _coinRepository: CoinRepository) : ViewModel() {
    private val _tmpTickerList: MutableList<Ticker> = mutableListOf()
    private val _tickerList: MutableLiveData<List<Ticker>> = MutableLiveData()
    val tickerList = _tickerList.asLiveData()

    val favoriteTickerList = Transformations.map(_tickerList) { tickerList ->
        tickerList.filter {
            it.isFavorite
        }
    }

    private val _doRetry: MutableLiveData<String> = MutableLiveData()
    val doRetry = _doRetry.asLiveData()

    private suspend fun getKRWTickers(): Boolean {
        return when (val tickerList = _coinRepository.getKRWTickers()) {
            is Resource.Success -> {
                _tmpTickerList.clear()
                _tmpTickerList.addAll(tickerList.data)
                _tickerList.value = _tmpTickerList.toList()
                true
            }
            else -> false
        }
    }

    fun doListenPrice() {
        viewModelScope.launch {
            if (_tmpTickerList.isEmpty()) {
                if (!getKRWTickers()) {
                    onSocketError("Error")
                    return@launch
                }
            }

            val requestTickerData = RequestTickerData(_tmpTickerList.map { it.symbol })
            _coinRepository.listenTickerSocket(requestTickerData).collect {
                when (it) {
                    is Resource.Success -> onReceivedTicker(it.data)
                    is Resource.Error -> onSocketError(it.message)
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun doRetryListenPrice() {
        _coinRepository.stopTickerSocket()
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
            _coinRepository.addFavoriteTicker(symbol)
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
            _coinRepository.deleteFavoriteTicker(symbol)
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
            }
        }
    }

    private fun onSocketError(message: String) {
        _coinRepository.stopTickerSocket()
        _doRetry.value = message
    }

    override fun onCleared() {
        super.onCleared()
        _coinRepository.stopTickerSocket()
    }
}