package com.example.mvvmbithumb.repository

import com.example.mvvmbithumb.model.TickerData
import com.example.mvvmbithumb.repository.websocket.WebSocketProvider
import kotlinx.coroutines.channels.Channel

class BithumbRepositoryImpl(private val _webSocketProvider: WebSocketProvider) : BithumbRepository {
    override fun startTickerSocket(): Channel<TickerData> {
        return _webSocketProvider.startTickerSocket()
    }

    override fun stopTickerSocket() {
        _webSocketProvider.stopTickerSocket()
    }
}