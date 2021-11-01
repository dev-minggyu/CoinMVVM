package com.example.mvvmbithumb.data.websocket.listener

import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerInfo
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import com.example.mvvmbithumb.data.websocket.exception.SocketAbortedException
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class TickerListener(val requestTickerData: RequestTickerData) : WebSocketListener() {
    val socketEventChannel = Channel<TickerData>()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val sendData = Gson().toJson(requestTickerData)
        webSocket.send(sendData)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            val tickerInfo = Gson().fromJson(text, TickerInfo::class.java)
            val tickerData = TickerData(tickerInfo)
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


