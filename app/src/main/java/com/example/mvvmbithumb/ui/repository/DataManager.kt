package com.example.mvvmbithumb.ui.repository

import com.example.mvvmbithumb.ui.data.websocket.dto.ticker.TickerData
import com.example.mvvmbithumb.ui.repository.websocket.WebSocketProvider
import kotlinx.coroutines.channels.Channel

class DataManager(private val _webSocketProvider: WebSocketProvider) {
    fun startTickerSocket(): Channel<TickerData> {
        return _webSocketProvider.startTickerSocket()
    }

    fun stopTickerSocket() {
        _webSocketProvider.stopTickerSocket()
    }
}