package com.example.mvvmbithumb.data.model

data class TransactionData(
    val content: TransactionContent? = null,
    val type: String? = null,
    val exception: Throwable? = null
)

data class TransactionContent(
    val list: List<TransactionInfo>
)

data class TransactionInfo(
    val buySellGb: String,
    val contAmt: String,
    val contDtm: String,
    val contPrice: String,
    val contQty: String,
    val symbol: String,
    val updn: String
)