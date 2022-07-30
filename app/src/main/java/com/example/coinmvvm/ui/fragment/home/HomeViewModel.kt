package com.example.coinmvvm.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinmvvm.constant.enums.SortState
import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.extension.asLiveData
import com.example.coinmvvm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val _coinRepository: CoinRepository
) : ViewModel() {
    private val _tmpTickerList: MutableList<Ticker> = mutableListOf()
    private val _tickerList: MutableLiveData<List<Ticker>> = MutableLiveData()
    val tickerList = _tickerList.asLiveData()
    val favoriteTickerList = Transformations.map(_tickerList) { tickerList ->
        tickerList.filter {
            it.isFavorite
        }
    }

    private val _socketError: MutableLiveData<String> = MutableLiveData()
    val socketError = _socketError.asLiveData()

    private var _isSocketClose = true

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
        if (_isSocketClose) {
            viewModelScope.launch {
                if (_tmpTickerList.isEmpty()) {
                    if (!getKRWTickers()) {
                        _isSocketClose = true
                        onSocketError("Error")
                        return@launch
                    }
                }

                val requestTickerData = RequestTickerData(_tmpTickerList.map { it.symbol })
                _coinRepository.tickerSocket(requestTickerData).collect {
                    when (it) {
                        is Resource.Success -> {
                            _isSocketClose = false
                            onReceivedTicker(it.data)
                        }
                        is Resource.Error -> {
                            _isSocketClose = true
                            onSocketError(it.message)
                        }
                        is Resource.Loading -> {}
                    }
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
            val index = _coinRepository.addFavoriteTicker(symbol)
            _tmpTickerList.find {
                it.symbol == symbol
            }?.apply {
                isFavorite = true
                favoriteIndex = index
            }
            _tickerList.value = _tmpTickerList
        }
    }

    fun deleteFavoriteSymbol(symbol: String) {
        viewModelScope.launch {
            _coinRepository.deleteFavoriteTicker(symbol)
            _tmpTickerList.find {
                it.symbol == symbol
            }?.apply {
                isFavorite = false
            }
            _tickerList.value = _tmpTickerList
        }
    }

    fun sortTicker(sortState: SortState) {
        when (sortState) {
            SortState.NO -> _tmpTickerList.sortBy { it.index }
            SortState.NAME_DESC -> _tmpTickerList.sortByDescending { it.symbol }
            SortState.NAME_ASC -> _tmpTickerList.sortBy { it.symbol }
            SortState.PRICE_DESC -> _tmpTickerList.sortByDescending { it.currentPrice.toFloat() }
            SortState.PRICE_ASC -> _tmpTickerList.sortBy { it.currentPrice.toFloat() }
        }
        _tickerList.value = _tmpTickerList
    }

    private fun onReceivedTicker(tickerData: TickerData?) {
        val tickerContent = tickerData?.ticker?.content
        tickerContent?.let { content ->
            _tmpTickerList.find {
                it.symbol == content.symbol
            }?.apply {
                currentPrice = content.closePrice
                prevPrice = content.prevClosePrice
            }
            _tickerList.value = _tmpTickerList
        }
    }

    private fun onSocketError(message: String) {
        _coinRepository.stopTickerSocket()
        _socketError.value = message
    }

    override fun onCleared() {
        super.onCleared()
        _coinRepository.stopTickerSocket()
    }
}