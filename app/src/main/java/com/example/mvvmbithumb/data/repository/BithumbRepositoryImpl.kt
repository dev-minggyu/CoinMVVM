package com.example.mvvmbithumb.data.repository

import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerList
import com.example.mvvmbithumb.data.network.NetworkRepository
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import kotlinx.coroutines.channels.Channel

class BithumbRepositoryImpl(
    private val _webSocketProvider: WebSocketProvider,
    private val _networkRepository: NetworkRepository
) : BithumbRepository {
    override fun listenTickerSocket(requestTickerData: RequestTickerData): Channel<TickerData> {
        return _webSocketProvider.tickerSocket.listenTickerSocket(requestTickerData)
    }

    override fun stopTickerSocket() {
        _webSocketProvider.tickerSocket.stopTickerSocket()
    }

    override suspend fun getKRWTickers(): TickerList {
        return _networkRepository.getKRWTickers()
    }
}