package com.example.mvvmbithumb.data.network

import com.example.mvvmbithumb.data.model.TickerList
import retrofit2.http.GET

interface NetworkAPI {
    @GET("public/ticker/ALL_KRW")
    suspend fun getKRWTickers(): TickerList
}