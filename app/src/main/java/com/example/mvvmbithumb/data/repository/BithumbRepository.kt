package com.example.mvvmbithumb.data.repository

import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerList
import kotlinx.coroutines.channels.Channel

interface BithumbRepository {
    fun listenTickerSocket(requestTickerData: RequestTickerData): Channel<TickerData>

    fun stopTickerSocket()

    suspend fun getKRWTickers(): TickerList

    suspend fun addFavoriteTicker(symbol: String)

    suspend fun deleteFavoriteTicker(symbol: String)
}