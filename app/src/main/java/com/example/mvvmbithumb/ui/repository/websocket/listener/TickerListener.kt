package com.example.mvvmbithumb.ui.repository.websocket.listener

import com.example.mvvmbithumb.ui.data.websocket.dto.ticker.Ticker
import com.example.mvvmbithumb.ui.data.websocket.dto.ticker.TickerData
import com.example.mvvmbithumb.ui.repository.websocket.WebSocketProvider
import com.example.mvvmbithumb.ui.repository.websocket.exception.SocketAbortedException
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class TickerListener : WebSocketListener() {
    val socketEventChannel = Channel<TickerData>()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send("{\"type\":\"ticker\", \"symbols\": [\"BTC_KRW\"], \"tickTypes\": [\"MID\"]}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            val ticker = Gson().fromJson(text, Ticker::class.java)
            val tickerData = TickerData(ticker)
            socketEventChannel.send(tickerData)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            socketEventChannel.send(TickerData(exception = SocketAbortedException()))
        }
        webSocket.close(WebSocketProvider.STATUS_NORMAL_CLOSURE, null)
        socketEventChannel.close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            socketEventChannel.send(TickerData(exception = t))
        }
    }
}


