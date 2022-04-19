package com.example.coinmvvm.data.remote.network

import com.example.coinmvvm.data.model.TickerList
import com.example.coinmvvm.util.Resource
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(private val networkAPI: NetworkAPI) : NetworkRepository {
    override suspend fun getKRWTickers(): Resource<TickerList> {
        return try {
            val response = networkAPI.getKRWTickers()
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message!!)
        }
    }

    companion object {
        const val BASE_URL = "https://api.bithumb.com/"
    }
}