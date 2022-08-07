package com.example.coinmvvm.data.model

import android.util.Log
import com.example.coinmvvm.App
import com.example.coinmvvm.R
import com.example.coinmvvm.constant.enums.CurrencyType
import com.example.coinmvvm.constant.enums.PriceState
import com.google.gson.Gson
import java.text.DecimalFormat

data class RequestTickerData(
    var symbols: List<String> = listOf(),
    val tickTypes: List<String> = listOf("24H"),
    val type: String = "ticker"
)

data class TickerData(
    val ticker: TickerInfo? = null
)

data class TickerInfo(
    val content: TickerContent,
    val type: String
)

data class TickerContent(
    val buyVolume: String,
    val chgAmt: String,
    val chgRate: String,
    val closePrice: Double,
    val date: String,
    val highPrice: String,
    val lowPrice: String,
    val openPrice: String,
    val prevClosePrice: Double,
    val sellVolume: String,
    val symbol: String,
    val tickType: String,
    val time: String,
    val value: String,
    val volume: String,
    val volumePower: String
)

data class Ticker(
    var index: Int,
    var symbol: String,
    var currencyType: CurrencyType,
    var currentPrice: Double,
    var prevPrice: Double,
    var transactionAmount: Double,
    var isFavorite: Boolean = false,
    var favoriteIndex: Long = -1
) {
    fun getSymbolName(): String {
        return symbol + "_" + currencyType.currency
    }

    fun getPriceState(): PriceState {
        return if (currentPrice == prevPrice) {
            PriceState.SAME
        } else {
            if (currentPrice > prevPrice) {
                PriceState.UP
            } else {
                PriceState.DOWN
            }
        }
    }

    fun getDecimalPrice(): String {
        val decimalFormat = DecimalFormat("#,###.####")
        return decimalFormat.format(currentPrice)
    }

    fun getVolume(): String {
        val decimalFormat = DecimalFormat("#,###")
        val amount = (transactionAmount / 1_000_000).toInt()
        return if (amount < 1) {
            decimalFormat.format(transactionAmount) + App.getString(R.string.unit_won)
        } else {
            decimalFormat.format(amount) + App.getString(R.string.unit_million)
        }
    }

    fun getRateOfChange(): Double {
        return ((currentPrice - prevPrice) / prevPrice) * 100
    }

    fun getRateOfChangeWithUnit(): String {
        val rate = ((currentPrice - prevPrice) / prevPrice) * 100
        return String.format("%.2f", rate) + "%"
    }
}

data class TickerList(
    val data: Map<String, Any>,
    val status: String
) {
    fun toKRWTickerList(): List<Ticker> {
        var index = 0
        return data.filter { it.key != "date" }.map {
            val symbolValue = Gson().fromJson(it.value.toString(), SymbolValue::class.java)
            Ticker(
                index++,
                it.key,
                CurrencyType.KRW,
                symbolValue.closing_price,
                symbolValue.prev_closing_price,
                symbolValue.acc_trade_value_24H
            )
        }
    }
}

data class SymbolValue(
    val acc_trade_value: Double,
    val acc_trade_value_24H: Double,
    val closing_price: Double,
    val fluctate_24H: String,
    val fluctate_rate_24H: String,
    val max_price: String,
    val min_price: String,
    val opening_price: String,
    val prev_closing_price: Double,
    val units_traded: String,
    val units_traded_24H: String
)