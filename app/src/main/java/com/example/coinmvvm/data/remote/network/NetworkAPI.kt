package com.example.coinmvvm.data.remote.network

import com.example.coinmvvm.data.model.TickerList
import retrofit2.Response
import retrofit2.http.GET

interface NetworkAPI {
    @GET("public/ticker/ALL_KRW")
    suspend fun getKRWTickers(): Response<TickerList>
}