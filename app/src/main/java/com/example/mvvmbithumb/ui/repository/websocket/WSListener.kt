package com.example.mvvmbithumb.ui.repository.websocket

import com.example.mvvmbithumb.ui.data.websocket.dto.TestData
import com.example.mvvmbithumb.ui.repository.websocket.exception.SocketAbortedException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WSListener(private val request: String) : WebSocketListener() {
    val socketEventChannel = Channel<TestData>()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send(request)
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
        webSocket.close(WSProvider.STATUS_NORMAL_CLOSURE, null)
        socketEventChannel.close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            socketEventChannel.send(TestData(exception = t))
        }
    }
}


