package com.example.coinmvvm.data.repository

import com.example.coinmvvm.data.local.db.CoinDatabase
import com.example.coinmvvm.data.local.db.entity.FavoriteSymbolEntity
import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.Ticker
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.remote.network.NetworkRepository
import com.example.coinmvvm.data.remote.websocket.WebSocketService
import com.example.coinmvvm.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val _webSocketService: WebSocketService,
    private val _networkRepository: NetworkRepository,
    private val _database: CoinDatabase
) : CoinRepository {

    private val _observeTickerSocket = MutableSharedFlow<Resource<List<Ticker>>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val observeTickerSocket = _observeTickerSocket.asSharedFlow()

    private var _isRunningTickerSocket = false

    override suspend fun listenTickerSocket() {
        if (_isRunningTickerSocket) return
        _isRunningTickerSocket = true

        withContext(Dispatchers.Default) {
            when (val tickerList = getKRWTickers()) {
                is Resource.Success -> {
                    val requestTickerData = RequestTickerData(tickerList.data.map { it.getSymbolName() })
                    _webSocketService.listenTickerSocket(requestTickerData).collect { tickerData ->
                        when (tickerData) {
                            is Resource.Success -> {
                                updateTickerData(tickerData.data, tickerList.data)
                                _observeTickerSocket.emit(
                                    Resource.Success(tickerList.data)
                                )
                            }
                            else -> {
                                _observeTickerSocket.emit(Resource.Error(null))
                                _isRunningTickerSocket = false
                            }
                        }
                    }
                }
                else -> {
                    _observeTickerSocket.emit(Resource.Error(null))
                    _isRunningTickerSocket = false
                }
            }
        }
    }

    private fun updateTickerData(tickerData: TickerData, tickerList: List<Ticker>) {
        val tickerContent = tickerData.ticker?.content
        tickerContent?.let { content ->
            tickerList.find {
                it.getSymbolName() == content.symbol
            }?.apply {
                currentPrice = content.closePrice
                prevPrice = content.prevClosePrice
            }
        }
    }

    override suspend fun observeTickerSocket(): SharedFlow<Resource<List<Ticker>>> = observeTickerSocket

    override suspend fun stopTickerSocket() {
        _webSocketService.stopTickerSocket()
    }

    override suspend fun getKRWTickers(): Resource<List<Ticker>> {
        return withContext(Dispatchers.IO) {
            when (val response = _networkRepository.getKRWTickers()) {
                is Resource.Success -> {
                    val tickerList = response.data.toKRWTickerList()
                    val favoriteList = getFavoriteSymbols()
                    tickerList.forEach { ticker ->
                        favoriteList.find {
                            it.symbol == ticker.getSymbolName()
                        }?.let {
                            ticker.isFavorite = true
                            ticker.favoriteIndex = it.id
                        }
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

    override suspend fun addFavoriteTicker(symbol: String): Long {
        return withContext(Dispatchers.IO) {
            _database.coinDao().addFavoriteTicker(FavoriteSymbolEntity(symbol = symbol))
        }
    }

    override suspend fun deleteFavoriteTicker(symbol: String) {
        withContext(Dispatchers.IO) {
            _database.coinDao().deleteFavoriteSymbol(symbol)
        }
    }
}