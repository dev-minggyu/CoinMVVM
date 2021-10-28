package com.example.mvvmbithumb.data.model

data class TickerData(
    val ticker: TickerInfo? = null,
    val exception: Throwable? = null
)

data class TickerInfo(
    val content: TickerContent,
    val type: String
)

data class TickerContent(
    val buyVolume: String,
    val chgAmt: String,
    val chgRate: String,
    val closePrice: String,
    val date: String,
    val highPrice: String,
    val lowPrice: String,
    val openPrice: String,
    val prevClosePrice: String,
    val sellVolume: String,
    val symbol: String,
    val tickType: String,
    val time: String,
    val value: String,
    val volume: String,
    val volumePower: String
)

data class Ticker(
    val symbol: String,
    var currentPrice: String = "0"
)