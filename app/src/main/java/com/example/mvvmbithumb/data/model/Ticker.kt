package com.example.mvvmbithumb.data.model

import com.google.gson.Gson

data class RequestTickerData(
    var symbols: List<String> = listOf(),
    val tickTypes: List<String> = listOf("1H"),
    val type: String = "ticker"
)

data class TickerData(
    val ticker: TickerInfo? = null, val exception: Throwable? = null
)

data class TickerInfo(
    val content: TickerContent, val type: String
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
    var symbol: String, var currentPrice: String = "0"
)

data class TickerList(
    val data: Map<String, Any>, val status: String
) {
    fun toKRWTickerList(): List<Ticker> {
        return data.filter { it.key != "date" }.map {
            val symbolValue = Gson().fromJson(it.value.toString(), SymbolValue::class.java)
            Ticker(it.key + "_KRW", symbolValue.closing_price)
        }
    }
}

data class SymbolValue(
    val acc_trade_value: String,
    val acc_trade_value_24H: String,
    val closing_price: String,
    val fluctate_24H: String,
    val fluctate_rate_24H: String,
    val max_price: String,
    val min_price: String,
    val opening_price: String,
    val prev_closing_price: String,
    val units_traded: String,
    val units_traded_24H: String
)