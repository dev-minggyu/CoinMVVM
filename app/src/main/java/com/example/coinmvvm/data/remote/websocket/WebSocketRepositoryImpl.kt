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

class WebSocketRepositoryImpl @Inject constructor(private val webSocket: HttpClient) : WebSocketRepository {
    override fun tickerSocket(requestTickerData: RequestTickerData): Flow<Resource<TickerData>> = flow {
        try {
            webSocket.wss(TICKER_URL) {
                val gson = Gson()
                val jsonData = gson.toJson(requestTickerData)
                outgoing.send(Frame.Text(jsonData))
                incoming.consumeEach {
                    when (it) {
                        is Frame.Text -> {
                            val tickerInfo = gson.fromJson(it.readText(), TickerInfo::class.java)
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

    override fun stopTickerSocket() {
        webSocket.close()
    }

    companion object {
        const val TICKER_URL = "wss://pubwss.bithumb.com/pub/ws"
    }
}