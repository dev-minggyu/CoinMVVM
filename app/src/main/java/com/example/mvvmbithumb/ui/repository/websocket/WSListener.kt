package com.example.mvvmbithumb.ui.repository.websocket

import com.example.mvvmbithumb.ui.data.websocket.dto.TestData
import com.example.mvvmbithumb.ui.repository.websocket.WSProvider.Companion.NORMAL_CLOSURE_STATUS
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WSListener : WebSocketListener() {
    val socketEventChannel: Channel<TestData> = Channel()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send("{\"type\":\"ticker\", \"symbols\": [\"BTC_KRW\"], \"tickTypes\": [\"1H\"]}")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            socketEventChannel.send(TestData(text))
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            socketEventChannel.send(TestData(exception = SocketAbortedException()))
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        socketEventChannel.close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            socketEventChannel.send(TestData(exception = t))
        }
    }
}

class SocketAbortedException : Exception()

