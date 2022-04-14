package com.example.coinmvvm.data.remote.websocket

import com.example.coinmvvm.data.remote.websocket.sockets.TickerSocket

class WebSocketProvider {
    val tickerSocket = TickerSocket()

    companion object {
        const val BASE_URL = "wss://pubwss.bithumb.com/pub/ws"
    }
}