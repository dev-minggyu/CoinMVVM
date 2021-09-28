package com.example.mvvmbithumb.ui.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmbithumb.ui.repository.websocket.WSProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class HomeViewModel(private val wsProvider: WSProvider) : ViewModel() {
    fun subscribeToSocketEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            wsProvider.startSocket(WSProvider.REQUEST_BITHUMB_TICKER).consumeEach {
                if (it.exception == null) {
                    onSocketReceived(it.message)
                } else {
                    onSocketError(it.exception)
                }
            }
        }
    }

    private fun onSocketReceived(message: String?) {
        println("Collecting : $message")
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        super.onCleared()
        wsProvider.stopSocket()
    }
}