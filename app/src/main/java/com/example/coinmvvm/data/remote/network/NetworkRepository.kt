package com.example.coinmvvm.data.remote.network

import com.example.coinmvvm.BuildConfig
import com.example.coinmvvm.data.model.TickerList
import com.example.coinmvvm.util.Resource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRepository {
    private val mAPI = retrofitClient().create(NetworkAPI::class.java)

    private fun retrofitClient(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }

    suspend fun getKRWTickers(): Resource<TickerList> {
        return try {
            val response = mAPI.getKRWTickers()
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
        private const val BASE_URL = "https://api.bithumb.com/"
    }
}