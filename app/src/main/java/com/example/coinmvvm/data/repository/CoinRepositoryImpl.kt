package com.example.coinmvvm.data.repository

import com.example.coinmvvm.data.local.db.CoinDatabase
import com.example.coinmvvm.data.local.db.entity.FavoriteSymbolEntity
import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.remote.network.NetworkRepository
import com.example.coinmvvm.data.remote.websocket.WebSocketProvider
import com.example.coinmvvm.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CoinRepositoryImpl(
    private val _webSocketProvider: WebSocketProvider,
    private val _networkRepository: NetworkRepository,
    private val _database: CoinDatabase
) : CoinRepository {

    override fun listenTickerSocket(requestTickerData: RequestTickerData): Flow<Resource<TickerData>> {
        return _webSocketProvider.tickerSocket.listenTickerSocket(requestTickerData)
    }

    override fun stopTickerSocket() {
        _webSocketProvider.tickerSocket.stopTickerSocket()
    }

    override suspend fun getKRWTickers(): Resource<List<Ticker>> {
        return withContext(Dispatchers.IO) {
            when (val response = _networkRepository.getKRWTickers()) {
                is Resource.Success -> {
                    val tickerList = response.data.toKRWTickerList()
                    val favoriteList = getFavoriteSymbols().map {
                        it.symbol
                    }
                    tickerList.forEach {
                        it.isFavorite = favoriteList.contains(it.symbol)
                    }
                    Resource.Success(tickerList)
                }
                else -> Resource.Error("Error")
            }
        }
    }

    override suspend fun getFavoriteSymbols(): List<FavoriteSymbolEntity> {
        return withContext(Dispatchers.IO) {
            _database.coinDao().getFavoriteSymbols()
        }
    }

    override suspend fun addFavoriteTicker(symbol: String) {
        withContext(Dispatchers.IO) {
            _database.coinDao().insertFavoriteSymbol(FavoriteSymbolEntity(symbol = symbol))
        }
    }

    override suspend fun deleteFavoriteTicker(symbol: String) {
        withContext(Dispatchers.IO) {
            _database.coinDao().deleteFavoriteSymbol(symbol)
        }
    }
}