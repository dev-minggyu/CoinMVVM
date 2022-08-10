package com.example.coinmvvm.data.remote.websocket

import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.util.Resource
import kotlinx.coroutines.flow.SharedFlow

interface WebSocketService {
    val observeTickerSocket: SharedFlow<Resource<TickerData>>

    suspend fun connectTickerSocket(): Resource<Unit>

    suspend fun requestTickerPrice(requestTickerData: RequestTickerData)

    suspend fun listenTickerSocket()

    suspend fun stopTickerSocket()
}