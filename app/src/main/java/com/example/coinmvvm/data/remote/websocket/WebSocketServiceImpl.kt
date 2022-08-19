package com.example.coinmvvm.data.remote.websocket

import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.model.TickerInfo
import com.example.coinmvvm.util.Resource
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WebSocketServiceImpl @Inject constructor(
    private val _client: HttpClient
) : WebSocketService {
    private var _tickerSocket: DefaultClientWebSocketSession? = null

    override suspend fun listenTickerSocket(requestTickerData: RequestTickerData): Flow<Resource<TickerData>> = flow {
        try {
            _client.wss(URL_TICKER) {
                _tickerSocket = this
                sendSerialized(requestTickerData)
                val gson = Gson()
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val tickerInfo = gson.fromJson(frame.readText(), TickerInfo::class.java)
                        val tickerData = TickerData(tickerInfo)
                        emit(Resource.Success(tickerData))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e.message))
        }
    }

    override suspend fun stopTickerSocket() {
        try {
            _tickerSocket?.run {
                close()
                incoming.cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val URL_TICKER = "wss://pubwss.bithumb.com/pub/ws"
    }
}