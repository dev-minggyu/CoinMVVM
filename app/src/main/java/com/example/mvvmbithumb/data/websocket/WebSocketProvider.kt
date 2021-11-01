package com.example.mvvmbithumb.data.websocket

import com.example.mvvmbithumb.data.websocket.sockets.TickerSocket
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class WebSocketProvider {
    val tickerSocket = TickerSocket()

    companion object {
        private const val BITHUMB_URL = "wss://pubwss.bithumb.com/pub/ws"

        val socketOkHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        val baseRequest = Request.Builder()
            .url(BITHUMB_URL)
            .build()

        const val STATUS_NORMAL_CLOSURE = 1000
    }
}