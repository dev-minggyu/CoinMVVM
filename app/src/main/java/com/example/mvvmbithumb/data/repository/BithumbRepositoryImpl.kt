package com.example.mvvmbithumb.data.repository

import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerList
import com.example.mvvmbithumb.data.network.NetworkRepository
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import kotlinx.coroutines.channels.Channel

class BithumbRepositoryImpl(private val _webSocketProvider: WebSocketProvider,
                            private val _networkRepository: NetworkRepository) : BithumbRepository {
    override fun startTickerSocket(): Channel<TickerData> {
        return _webSocketProvider.startTickerSocket()
    }

    override fun stopTickerSocket() {
        _webSocketProvider.stopTickerSocket()
    }

    override suspend fun getKRWTickers(): TickerList {
        return _networkRepository.getKRWTickers()
    }
}