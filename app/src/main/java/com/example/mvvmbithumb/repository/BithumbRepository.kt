package com.example.mvvmbithumb.repository

import com.example.mvvmbithumb.model.TickerData
import kotlinx.coroutines.channels.Channel

interface BithumbRepository {
    fun startTickerSocket(): Channel<TickerData>

    fun stopTickerSocket()
}