package com.example.mvvmbithumb.data.websocket.sockets

import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerInfo
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import com.example.mvvmbithumb.util.Resource
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TickerSocket {
    private lateinit var _tickerSocket: HttpClient

    fun listenTickerSocket(requestTickerData: RequestTickerData): Flow<Resource<TickerData>> = flow {
        try {
            _tickerSocket = HttpClient(OkHttp) {
                install(WebSockets)
            }

            _tickerSocket.wss(WebSocketProvider.BITHUMB_URL) {
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
        _tickerSocket.close()
    }
}

