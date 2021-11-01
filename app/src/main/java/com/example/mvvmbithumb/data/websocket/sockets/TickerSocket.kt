package com.example.mvvmbithumb.data.websocket.sockets

import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerInfo
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import com.example.mvvmbithumb.data.websocket.exception.SocketAbortedException
import com.example.mvvmbithumb.extension.sendIgnoreClosed
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class TickerSocket {
    private var _tickerSocket: WebSocket? = null
    private var _tickerListener: TickerListener? = null

    fun listenTickerSocket(requestTickerData: RequestTickerData): Channel<TickerData> {
        _tickerListener = TickerListener(requestTickerData)
        return with(_tickerListener!!) {
            _tickerSocket = WebSocketProvider.socketOkHttpClient.newWebSocket(
                WebSocketProvider.baseRequest, this
            )
            this.socketEventChannel
        }
    }

    fun stopTickerSocket() {
        try {
            _tickerSocket?.close(WebSocketProvider.STATUS_NORMAL_CLOSURE, null)
            _tickerSocket = null
            _tickerListener?.socketEventChannel?.close()
            _tickerListener = null
        } catch (ex: Exception) {
        }
    }
}

class TickerListener(private val requestTickerData: RequestTickerData) : WebSocketListener() {
    val socketEventChannel = Channel<TickerData>()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val sendData = Gson().toJson(requestTickerData)
        webSocket.send(sendData)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            val tickerInfo = Gson().fromJson(text, TickerInfo::class.java)
            val tickerData = TickerData(tickerInfo)
            socketEventChannel.sendIgnoreClosed(tickerData)
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            socketEventChannel.sendIgnoreClosed(TickerData(exception = SocketAbortedException()))
        }
        webSocket.close(WebSocketProvider.STATUS_NORMAL_CLOSURE, null)
        socketEventChannel.close()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            socketEventChannel.sendIgnoreClosed(TickerData(exception = t))
        }
    }
}


