package com.example.coinmvvm.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinmvvm.App.Companion.getString
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.SortCategory
import com.example.coinmvvm.constant.enums.SortModel
import com.example.coinmvvm.constant.enums.SortType
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

    var sortModel = SortModel(SortCategory.NAME, SortType.NO)

    private var _filterTickerSymbol: String = ""

    private val _socketError: MutableSingleLiveData<String> = MutableSingleLiveData()
    val socketError = _socketError.asSingleLiveData()

    private var _isSocketClose = true

    val filterTickerSymbol = { text: String ->
        _filterTickerSymbol = text
        notifySortedTickerList()
    }

    val onTickerSortClick = { sortModel: SortModel ->
        sortTicker(sortModel)
    }

    init {
        viewModelScope.launch {
            _coinRepository.observeTickerSocket().collect {
                when (it) {
                    is Resource.Success -> onReceivedTicker(it.data)
                    is Resource.Error -> {
                        _isSocketClose = true
                        onError(it.message ?: getString(R.string.error_getting_coin_list))
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun getKRWTickers(): Boolean {
        return when (val tickerList = _coinRepository.getKRWTickers()) {
            is Resource.Success -> {
                _tmpTickerList.clear()
                _tmpTickerList.addAll(tickerList.data)
                notifySortedTickerList()
                true
            }
            else -> false
        }
    }

    fun observeTickerPrice() {
        if (_isSocketClose) {
            _isSocketClose = false
            viewModelScope.launch {
                if (!getKRWTickers()) {
                    _isSocketClose = true
                    onError(getString(R.string.error_getting_coin_list))
                    return@launch
                }

                when (_coinRepository.initTickerSocket()) {
                    is Resource.Success -> {
                        val requestTickerData = RequestTickerData(_tmpTickerList.map { it.getSymbolName() })
                        _coinRepository.requestTickerPrice(requestTickerData)
                    }
                    is Resource.Error -> {
                        _isSocketClose = true
                        onError(getString(R.string.error_getting_coin_list))
                    }
                    else -> {}
                }
            }
        }
    }

    fun stopTickerSocket() {
        viewModelScope.launch {
            _coinRepository.stopTickerSocket()
        }
    }

    fun retryListenPrice() {
        stopTickerSocket()
        observeTickerPrice()
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

    private fun notifySortedTickerList() {
        sortTicker()
        _tickerList.value = filterTicker()
    }

    private fun sortTicker(sortModel: SortModel) {
        this.sortModel = sortModel
        _tickerList.value = null
        notifySortedTickerList()
    }

    private fun sortTicker() {
        sortModel.apply {
            when (category) {
                SortCategory.NAME -> {
                    when (type) {
                        SortType.NO -> _tmpTickerList.sortBy { it.index }
                        SortType.DESC -> _tmpTickerList.sortByDescending { it.symbol }
                        SortType.ASC -> _tmpTickerList.sortBy { it.symbol }
                    }
                }

                SortCategory.PRICE -> {
                    when (type) {
                        SortType.NO -> _tmpTickerList.sortBy { it.index }
                        SortType.DESC -> _tmpTickerList.sortByDescending { it.currentPrice.toFloat() }
                        SortType.ASC -> _tmpTickerList.sortBy { it.currentPrice.toFloat() }
                    }
                }

                SortCategory.RATE -> {
                    when (type) {
                        SortType.NO -> _tmpTickerList.sortBy { it.index }
                        SortType.DESC -> _tmpTickerList.sortByDescending { it.getRateOfChange() }
                        SortType.ASC -> _tmpTickerList.sortBy { it.getRateOfChange() }
                    }
                }

                SortCategory.VOLUME -> {
                    when (type) {
                        SortType.NO -> _tmpTickerList.sortBy { it.index }
                        SortType.DESC -> _tmpTickerList.sortByDescending { it.transactionAmount.toFloat() }
                        SortType.ASC -> _tmpTickerList.sortBy { it.transactionAmount.toFloat() }
                    }
                }
            }
        }
    }

    private fun filterTicker(): List<Ticker> {
        return if (_filterTickerSymbol.isNotEmpty()) {
            _tmpTickerList.filter { it.symbol.startsWith(_filterTickerSymbol, true) }
        } else {
            _tmpTickerList
        }
    }

    private fun onReceivedTicker(tickerData: TickerData?) {
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
        stopTickerSocket()
    }
}