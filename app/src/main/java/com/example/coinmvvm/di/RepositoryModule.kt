package com.example.coinmvvm.di

import com.example.coinmvvm.data.local.db.CoinDatabase
import com.example.coinmvvm.data.remote.network.NetworkAPI
import com.example.coinmvvm.data.remote.network.NetworkRepository
import com.example.coinmvvm.data.remote.websocket.WebSocketRepository
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.data.repository.CoinRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
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

    @Provides
    @Singleton
    fun provideWebSocketRepository(webSocket: HttpClient): WebSocketRepository {
        return WebSocketRepository(webSocket)
    }
}