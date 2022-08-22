package com.example.coinmvvm.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinmvvm.App.Companion.getString
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.SortCategory
import com.example.coinmvvm.constant.enums.SortModel
import com.example.coinmvvm.constant.enums.SortType
import com.example.coinmvvm.data.model.Ticker
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

    private val _sortModel: MutableLiveData<SortModel> = MutableLiveData(SortModel(SortCategory.NAME, SortType.NO))
    val sortModel = _sortModel.asLiveData()

    private var _filterTickerSymbol: String = ""

    private val _socketError: MutableSingleLiveData<String> = MutableSingleLiveData()
    val socketError = _socketError.asSingleLiveData()

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
                    is Resource.Success -> {
                        _tmpTickerList.clear()
                        _tmpTickerList.addAll(it.data)
                        notifySortedTickerList()
                    }
                    else -> {
                        onError(getString(R.string.error_getting_coin_list))
                    }
                }
            }
        }
    }

    fun retryListenPrice() {
        viewModelScope.launch {
            _coinRepository.stopTickerSocket()
            _coinRepository.listenTickerSocket()
        }
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
        _tickerList.value = filteredTickerList()
    }

    private fun sortTicker(sortModel: SortModel) {
        _sortModel.value = sortModel
        _tickerList.value = null
        notifySortedTickerList()
    }

    private fun sortTicker() {
        val (sortCategory, sortType) = sortModel.value!!

        _tmpTickerList.apply {
            if (sortType == SortType.NO) return sortBy { it.index }

            when (sortCategory) {
                SortCategory.NAME ->
                    when {
                        sortType == SortType.DESC -> sortByDescending { it.symbol }
                        sortType == SortType.ASC -> sortBy { it.symbol }
                    }

                SortCategory.PRICE ->
                    when {
                        sortType == SortType.DESC -> sortByDescending { it.currentPrice.toFloat() }
                        sortType == SortType.ASC -> sortBy { it.currentPrice.toFloat() }
                    }

                SortCategory.RATE ->
                    when {
                        sortType == SortType.DESC -> sortByDescending { it.getRateOfChange() }
                        sortType == SortType.ASC -> sortBy { it.getRateOfChange() }
                    }

                SortCategory.VOLUME ->
                    when {
                        sortType == SortType.DESC -> sortByDescending { it.transactionAmount.toFloat() }
                        sortType == SortType.ASC -> sortBy { it.transactionAmount.toFloat() }
                    }
            }
        }
    }

    private fun filteredTickerList(): List<Ticker> {
        return if (_filterTickerSymbol.isNotEmpty()) {
            _tmpTickerList.filter { it.symbol.startsWith(_filterTickerSymbol, true) }
        } else {
            _tmpTickerList
        }
    }

    private fun onError(message: String) {
        _socketError.setValue(message)
    }
}