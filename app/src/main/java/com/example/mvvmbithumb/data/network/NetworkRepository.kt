package com.example.mvvmbithumb.data.network

import com.example.mvvmbithumb.BuildConfig
import com.example.mvvmbithumb.data.model.TickerList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRepository {
    private val mRetrofit = retrofitClient()

    private val mAPI = mRetrofit.create(NetworkAPI::class.java)

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

    suspend fun getKRWTickers(): TickerList {
        return mAPI.getKRWTickers()
    }

    companion object {
        private const val BASE_URL = "https://api.bithumb.com/"
    }
}