package com.example.mvvmbithumb.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmbithumb.extension.asLiveData
import com.example.mvvmbithumb.model.TickerData
import com.example.mvvmbithumb.repository.BithumbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val _bithumbRepository: BithumbRepository) : ViewModel() {
    private val _btcPrice: MutableLiveData<Int> = MutableLiveData()
    val btcPrice = _btcPrice.asLiveData()

    fun subscribePrice() {
        viewModelScope.launch(Dispatchers.IO) {
            _bithumbRepository.startTickerSocket().consumeEach {
                if (it.exception == null) {
                    withContext(Dispatchers.Main) {
                        onTickerReceived(it)
                    }
                } else {
                    onSocketError(it.exception)
                }
            }
        }
    }

    private fun onTickerReceived(tickerData: TickerData?) {
        _btcPrice.value = tickerData?.ticker?.content?.closePrice?.toInt()
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        super.onCleared()
        _bithumbRepository.stopTickerSocket()
    }
}