package com.example.mvvmbithumb.ui.data.websocket.dto.ticker

data class TickerData(
    val ticker: Ticker? = null,
    val exception: Throwable? = null
)