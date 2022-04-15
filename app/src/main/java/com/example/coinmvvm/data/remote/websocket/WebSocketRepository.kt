package com.example.coinmvvm.data.remote.websocket

import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.model.TickerInfo
import com.example.coinmvvm.util.Resource
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WebSocketRepository {
    private var _tickerSocket: HttpClient? = null

    fun tickerSocket(requestTickerData: RequestTickerData): Flow<Resource<TickerData>> = flow {
        try {
            _tickerSocket = HttpClient(OkHttp) {
                install(WebSockets)
            }

            _tickerSocket!!.wss(TICKER_URL) {
                val jsonData = Gson().toJson(requestTickerData)
                outgoing.send(Frame.Text(jsonData))
                incoming.consumeEach {
                    when (it) {
                        is Frame.Text -> {
                            val tickerInfo = Gson().fromJson(it.readText(), TickerInfo::class.java)
                            val tickerData = TickerData(tickerInfo)
                            emit(Resource.Success(tickerData))
                        }
                        is Frame.Close -> {
                            emit(Resource.Error(closeReason.await()!!.message))
                        }
                        else -> {
                            emit(Resource.Error("Unknown Error"))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }

    fun stopTickerSocket() {
        _tickerSocket?.close()
    }

    companion object {
        const val TICKER_URL = "wss://pubwss.bithumb.com/pub/ws"
    }
}