package com.example.mvvmbithumb.data.remote.websocket

import com.example.mvvmbithumb.data.remote.websocket.sockets.TickerSocket

class WebSocketProvider {
    val tickerSocket = TickerSocket()

    companion object {
        const val BITHUMB_URL = "wss://pubwss.bithumb.com/pub/ws"
    }
}