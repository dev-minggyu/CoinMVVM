package com.example.mvvmbithumb.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.Ticker
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.repository.BithumbRepository
import com.example.mvvmbithumb.extension.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val _bithumbRepository: BithumbRepository) : ViewModel() {
    private val _tmpTickerList: MutableList<Ticker> = mutableListOf()
    private val _tickerList: MutableLiveData<List<Ticker>> = MutableLiveData()
    val tickerList = _tickerList.asLiveData()

    private val _doRetry: MutableLiveData<String> = MutableLiveData()
    val doRetry = _doRetry.asLiveData()

    private suspend fun getKRWTickers() {
        val tickerList = _bithumbRepository.getKRWTickers()
        _tmpTickerList.clear()
        _tmpTickerList.addAll(tickerList.toKRWTickerList())
    }

    fun doListenPrice() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_tmpTickerList.isEmpty()) {
                getKRWTickers()
            }

            val requestTickerData = RequestTickerData(_tmpTickerList.map { it.symbol })
            _bithumbRepository.listenTickerSocket(requestTickerData).consumeEach {
                if (it.exception == null) {
                    withContext(Dispatchers.Main) {
                        onReceivedTicker(it)
                    }
                } else {
                    onSocketError(it.exception)
                }
            }
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

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
        _bithumbRepository.stopTickerSocket()
        _doRetry.value = ex.message
    }

    override fun onCleared() {
        super.onCleared()
        _bithumbRepository.stopTickerSocket()
    }
}