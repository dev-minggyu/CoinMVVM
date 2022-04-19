package com.example.coinmvvm.data.remote.network

import com.example.coinmvvm.data.model.TickerList
import com.example.coinmvvm.util.Resource

interface NetworkRepository {
    suspend fun getKRWTickers(): Resource<TickerList>
}