package com.example.mvvmbithumb.ui.fragment.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmbithumb.data.model.TickerContent
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.repository.BithumbRepository
import com.example.mvvmbithumb.extension.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel(private val _bithumbRepository: BithumbRepository) : ViewModel() {
    private val _tickerList: MutableLiveData<List<TickerContent>> = MutableLiveData()
    val tickerList = _tickerList.asLiveData()

    fun subscribePrice() {
        viewModelScope.launch(Dispatchers.IO) {
            _bithumbRepository.startTickerSocket().consumeEach {
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

    fun getKRWTickers() {
        viewModelScope.launch(Dispatchers.IO) {
            val tickerList = _bithumbRepository.getKRWTickers()
            val tickers = tickerList.data.keys.toMutableList().removeLast()
            for (ticker in tickers) {
                Log.e("getKRWTickers", "ticker : " + ticker)
            }
        }
    }

    private fun onReceivedTicker(tickerData: TickerData?) {
        Log.e("onReceivedTicker", tickerData?.ticker?.content.toString())
//        _tickerList.value =
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        super.onCleared()
        _bithumbRepository.stopTickerSocket()
    }
}