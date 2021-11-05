package com.example.mvvmbithumb.data.repository

import com.example.mvvmbithumb.data.local.db.entity.FavoriteSymbolEntity
import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.Ticker
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerList
import com.example.mvvmbithumb.util.Resource
import kotlinx.coroutines.channels.Channel

interface BithumbRepository {
    fun listenTickerSocket(requestTickerData: RequestTickerData): Channel<Resource<TickerData>>

    fun stopTickerSocket()

    suspend fun getKRWTickers(): Resource<List<Ticker>>

    suspend fun getFavoriteSymbols(): List<FavoriteSymbolEntity>

    suspend fun addFavoriteTicker(symbol: String)

    suspend fun deleteFavoriteTicker(symbol: String)
}