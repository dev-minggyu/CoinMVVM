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

    fun startSocket(): Channel<TestData> {
        _webSocketListener = WSListener()
        return with(_webSocketListener!!) {
            _webSocket = socketOkHttpClient.newWebSocket(
                Request.Builder().url("wss://pubwss.bithumb.com/pub/ws").build(), this
            )
            socketOkHttpClient.dispatcher.executorService.shutdown()
            this.socketEventChannel
        }
    }

    fun stopSocket() {
        try {
            _webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            _webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}