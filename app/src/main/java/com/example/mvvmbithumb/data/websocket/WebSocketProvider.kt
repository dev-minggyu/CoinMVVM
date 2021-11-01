package com.example.mvvmbithumb.data.websocket

import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.websocket.listener.TickerListener
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WebSocketProvider {
    private val _socketOkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _baseRequest = Request.Builder().url(BITHUMB_URL).build()

    private var _tickerSocket: WebSocket? = null
    private var _tickerListener: TickerListener? = null

    fun listenTickerSocket(requestTickerData: RequestTickerData): Channel<TickerData> {
        _tickerListener = TickerListener(requestTickerData)
        return with(_tickerListener!!) {
            _tickerSocket = _socketOkHttpClient.newWebSocket(
                _baseRequest, this
            )
            this.socketEventChannel
        }
    }

    fun stopTickerSocket() {
        try {
            _tickerSocket?.close(STATUS_NORMAL_CLOSURE, null)
            _tickerSocket = null
            _tickerListener?.socketEventChannel?.close()
            _tickerListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        private const val BITHUMB_URL = "wss://pubwss.bithumb.com/pub/ws"

        const val STATUS_NORMAL_CLOSURE = 1000
    }
}