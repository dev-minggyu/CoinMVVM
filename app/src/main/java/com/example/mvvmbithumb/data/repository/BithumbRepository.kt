package com.example.mvvmbithumb.data.repository

import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerList
import kotlinx.coroutines.channels.Channel

interface BithumbRepository {
    fun startTickerSocket(): Channel<TickerData>

    fun stopTickerSocket()

    suspend fun getKRWTickers(): TickerList
}