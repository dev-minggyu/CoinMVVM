package com.example.coinmvvm.data.remote.websocket

import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.model.TickerInfo
import com.example.coinmvvm.util.Resource
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WebSocketRepositoryImpl @Inject constructor(
    private val _client: HttpClient
) : WebSocketRepository {
    private var webSocket: WebSocketSession? = null

    override fun observeTickerSocket(requestTickerData: RequestTickerData): Flow<Resource<TickerData>> = flow {
        try {
            _client.wss(TICKER_URL) {
                webSocket = this
                sendSerialized(requestTickerData)
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val tickerInfo = receiveDeserialized<TickerInfo>()
                        val tickerData = TickerData(tickerInfo)
                        emit(Resource.Success(tickerData))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    override suspend fun stopTickerSocket() {
        try {
            webSocket?.close()
            webSocket?.incoming?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TICKER_URL = "wss://pubwss.bithumb.com/pub/ws"
    }
}