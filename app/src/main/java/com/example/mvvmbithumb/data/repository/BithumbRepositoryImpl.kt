package com.example.mvvmbithumb.data.repository

import com.example.mvvmbithumb.data.local.db.BithumbDatabase
import com.example.mvvmbithumb.data.local.db.entity.FavoriteSymbolEntity
import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerList
import com.example.mvvmbithumb.data.network.NetworkRepository
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import com.example.mvvmbithumb.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext

class BithumbRepositoryImpl(
    private val _webSocketProvider: WebSocketProvider,
    private val _networkRepository: NetworkRepository,
    private val _database: BithumbDatabase
) : BithumbRepository {
    override fun listenTickerSocket(requestTickerData: RequestTickerData): Channel<Resource<TickerData>> {
        return _webSocketProvider.tickerSocket.listenTickerSocket(requestTickerData)
    }

    override fun stopTickerSocket() {
        _webSocketProvider.tickerSocket.stopTickerSocket()
    }

    override suspend fun getKRWTickers(): Resource<TickerList> {
        return withContext(Dispatchers.IO) {
            _networkRepository.getKRWTickers()
        }
    }

    override suspend fun getFavoriteSymbols(): List<FavoriteSymbolEntity> {
        return withContext(Dispatchers.IO) {
            _database.bithumbDao().getFavoriteSymbols()
        }
    }

    override suspend fun addFavoriteTicker(symbol: String) {
        withContext(Dispatchers.IO) {
            _database.bithumbDao().insertFavoriteSymbol(FavoriteSymbolEntity(symbol = symbol))
        }
    }

    override suspend fun deleteFavoriteTicker(symbol: String) {
        withContext(Dispatchers.IO) {
            _database.bithumbDao().deleteFavoriteSymbol(symbol)
        }
    }
}