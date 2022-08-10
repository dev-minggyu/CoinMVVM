package com.example.coinmvvm.data.remote.websocket

import com.example.coinmvvm.data.model.RequestTickerData
import com.example.coinmvvm.data.model.TickerData
import com.example.coinmvvm.data.model.TickerInfo
import com.example.coinmvvm.util.Resource
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class WebSocketServiceImpl @Inject constructor(
    private val _client: HttpClient
) : WebSocketService {
    private var _webSocket: DefaultClientWebSocketSession? = null
    private var _jobGlobalScope: Job? = null

    override suspend fun connectTickerSocket(): Resource<Unit> {
        return try {
            _webSocket = _client.webSocketSession { url(URL_TICKER) }
            if (_webSocket?.isActive == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(null)
        }
    }

    override suspend fun requestTickerPrice(requestTickerData: RequestTickerData) {
        try {
            _webSocket?.run { sendSerialized(requestTickerData) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val _observeTickerSocket = MutableSharedFlow<Resource<TickerData>>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val observeTickerSocket = _observeTickerSocket.asSharedFlow()

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun listenTickerSocket() {
        if (_jobGlobalScope == null) {
            _webSocket?.run {
                _jobGlobalScope = GlobalScope.launch {
                    try {
                        incoming.consumeEach { frame ->
                            if (frame is Frame.Text) {
                                val tickerInfo = receiveDeserialized<TickerInfo>()
                                val tickerData = TickerData(tickerInfo)
                                _observeTickerSocket.emit(Resource.Success(tickerData))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _observeTickerSocket.emit(Resource.Error(e.message))
                    }
                }
            }
        }
    }

    override suspend fun stopTickerSocket() {
        try {
            _webSocket?.run {
                close()
                incoming.cancel()
                _jobGlobalScope?.cancel()
                _jobGlobalScope = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val URL_TICKER = "wss://pubwss.bithumb.com/pub/ws"
    }
}