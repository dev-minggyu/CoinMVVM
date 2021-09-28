package com.example.mvvmbithumb.repository

import com.example.mvvmbithumb.data.websocket.dto.ticker.TickerData
import com.example.mvvmbithumb.repository.websocket.WebSocketProvider
import kotlinx.coroutines.channels.Channel

class DataManager(private val _webSocketProvider: WebSocketProvider) {
    fun startTickerSocket(): Channel<TickerData> {
        return _webSocketProvider.startTickerSocket()
    }

    fun stopTickerSocket() {
        _webSocketProvider.stopTickerSocket()
    }
}