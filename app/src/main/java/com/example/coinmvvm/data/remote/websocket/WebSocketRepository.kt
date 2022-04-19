package com.example.coinmvvm.data.remote.websocket

import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.util.Resource
import kotlinx.coroutines.flow.Flow

interface WebSocketRepository {
    fun tickerSocket(requestTickerData: RequestTickerData): Flow<Resource<TickerData>>

    fun stopTickerSocket()
}