package com.example.coinmvvm.di

import android.content.Context
import androidx.room.Room
import com.example.coinmvvm.BuildConfig
import com.example.coinmvvm.data.local.db.CoinDao
import com.example.coinmvvm.data.local.db.CoinDatabase
import com.example.coinmvvm.data.remote.network.NetworkAPI
import com.example.coinmvvm.data.remote.network.NetworkRepository
import com.example.coinmvvm.data.remote.websocket.WebSocketRepository
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.data.repository.CoinRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiModule {
    @Singleton
    @Provides
    fun provideCoinRepository(
        webSocketRepository: WebSocketRepository,
        networkRepository: NetworkRepository,
        coinDatabase: CoinDatabase
    ): CoinRepository {
        return CoinRepositoryImpl(
            webSocketRepository,
            networkRepository,
            coinDatabase
        )
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(networkAPI: NetworkAPI): NetworkRepository {
        return NetworkRepository(networkAPI)
    }

    @Singleton
    @Provides
    fun provideRetrofitClient(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(NetworkRepository.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkAPI(retrofit: Retrofit): NetworkAPI {
        return retrofit.create(NetworkAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideWebSocketRepository(webSocket: HttpClient): WebSocketRepository {
        return WebSocketRepository(webSocket)
    }

    @Provides
    @Singleton
    fun provideWebSocket(): HttpClient {
        return HttpClient(OkHttp) {
            install(WebSockets)
        }
    }

    @Singleton
    @Provides
    fun provideCoinDatabase(@ApplicationContext context: Context): CoinDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CoinDatabase::class.java, CoinDatabase.DB_FILE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideCoinDao(coinDB: CoinDatabase): CoinDao {
        return coinDB.coinDao()
    }
}