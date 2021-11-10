package com.example.mvvmbithumb.ui.fragment.home

import androidx.lifecycle.*
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
        return when (val tickerList = _bithumbRepository.getKRWTickers()) {
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
            _bithumbRepository.listenTickerSocket(viewModelScope, requestTickerData).consumeEach {
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
            }
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