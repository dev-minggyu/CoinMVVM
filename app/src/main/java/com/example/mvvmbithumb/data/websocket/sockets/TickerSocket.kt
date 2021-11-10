package com.example.mvvmbithumb.data.websocket.sockets

import com.example.mvvmbithumb.data.model.RequestTickerData
import com.example.mvvmbithumb.data.model.TickerData
import com.example.mvvmbithumb.data.model.TickerInfo
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import com.example.mvvmbithumb.extension.sendIgnoreClosed
import com.example.mvvmbithumb.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class TickerSocket {
    private var _tickerSocket: WebSocket? = null
    private var _tickerListener: TickerListener? = null

    private lateinit var _coroutineScope: CoroutineScope

    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        _coroutineScope = coroutineScope
    }

    fun listenTickerSocket(requestTickerData: RequestTickerData): Channel<Resource<TickerData>> {
        if (_tickerListener == null) {
            _tickerListener = TickerListener(requestTickerData)
        }

        return with(_tickerListener!!) {
            if (_tickerSocket == null) {
                _tickerSocket = WebSocketProvider.socketOkHttpClient.newWebSocket(
                    WebSocketProvider.baseRequest, this
                )
            }
            socketEventChannel
        }
    }

    fun stopTickerSocket() {
        _tickerSocket?.close(WebSocketProvider.STATUS_NORMAL_CLOSURE, null)
        _tickerSocket = null

        _tickerListener?.socketEventChannel?.close()
        _tickerListener = null
    }

    inner class TickerListener(private val requestTickerData: RequestTickerData) : WebSocketListener() {
        val socketEventChannel = Channel<Resource<TickerData>>()

        override fun onOpen(webSocket: WebSocket, response: Response) {
            val sendData = Gson().toJson(requestTickerData)
            webSocket.send(sendData)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            _coroutineScope.launch {
                val tickerInfo = Gson().fromJson(text, TickerInfo::class.java)
                val tickerData = TickerData(tickerInfo)
                socketEventChannel.sendIgnoreClosed(Resource.Success(tickerData))
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            _coroutineScope.launch {
                socketEventChannel.sendIgnoreClosed(Resource.Error(reason))
            }
            webSocket.close(WebSocketProvider.STATUS_NORMAL_CLOSURE, null)
            socketEventChannel.close()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _coroutineScope.launch {
                socketEventChannel.sendIgnoreClosed(Resource.Error(t.message ?: "Unknown"))
            }
        }
    }

}

