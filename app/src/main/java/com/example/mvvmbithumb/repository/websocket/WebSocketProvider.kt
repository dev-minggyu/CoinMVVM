package com.example.mvvmbithumb.repository.websocket

import com.example.mvvmbithumb.model.TickerData
import com.example.mvvmbithumb.repository.websocket.listener.TickerListener
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

    private var _webSocketListener: TickerListener? = null

    fun startTickerSocket(): Channel<TickerData> {
        _webSocketListener = TickerListener()
        return with(_webSocketListener!!) {
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
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        private const val BITHUMB_URL = "wss://pubwss.bithumb.com/pub/ws"

        const val STATUS_NORMAL_CLOSURE = 1000
    }
}