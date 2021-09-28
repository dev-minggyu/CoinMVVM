package com.example.mvvmbithumb.ui.repository.websocket

import com.example.mvvmbithumb.ui.data.websocket.dto.TestData
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WSProvider {
    private var _webSocket: WebSocket? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private var _webSocketListener: WSListener? = null

    fun startSocket(request: String): Channel<TestData> {
        _webSocketListener = WSListener(request)
        return with(_webSocketListener!!) {
            _webSocket = socketOkHttpClient.newWebSocket(
                Request.Builder().url(BITHUMB_URL).build(), this
            )
            socketOkHttpClient.dispatcher.executorService.shutdown()
            this.socketEventChannel
        }
    }

    fun stopSocket() {
        try {
            _webSocket?.close(STATUS_NORMAL_CLOSURE, null)
            _webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        private const val BITHUMB_URL = "wss://pubwss.bithumb.com/pub/ws"

        const val STATUS_NORMAL_CLOSURE = 1000

        const val REQUEST_BITHUMB_TICKER =
            "{\"type\":\"ticker\", \"symbols\": [\"BTC_KRW\"], \"tickTypes\": [\"1H\"]}"
    }
}