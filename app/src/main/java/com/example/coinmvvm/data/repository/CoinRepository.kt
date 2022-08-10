package com.example.coinmvvm.data.repository

import com.example.coinmvvm.data.local.db.entity.FavoriteSymbolEntity
import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.util.Resource
import kotlinx.coroutines.flow.SharedFlow

interface CoinRepository {
    suspend fun initTickerSocket(): Resource<Unit>

    suspend fun requestTickerPrice(requestTickerData: RequestTickerData)

    suspend fun observeTickerSocket(): SharedFlow<Resource<TickerData>>

    suspend fun stopTickerSocket()

    suspend fun getKRWTickers(): Resource<List<Ticker>>

    suspend fun getFavoriteSymbols(): List<FavoriteSymbolEntity>

    suspend fun addFavoriteTicker(symbol: String): Long

    suspend fun deleteFavoriteTicker(symbol: String)
}