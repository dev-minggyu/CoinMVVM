package com.example.mvvmbithumb.ui.data.websocket.dto

import okio.ByteString

data class TestData(
    val text: String? = null,
    val byteString: ByteString? = null,
    val exception: Throwable? = null
)