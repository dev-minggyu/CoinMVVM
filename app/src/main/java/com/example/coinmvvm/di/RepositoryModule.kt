package com.example.coinmvvm.di

import com.example.coinmvvm.data.remote.network.NetworkRepository
import com.example.coinmvvm.data.remote.network.NetworkRepositoryImpl
import com.example.coinmvvm.data.remote.websocket.WebSocketRepository
import com.example.coinmvvm.data.remote.websocket.WebSocketRepositoryImpl
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.data.repository.CoinRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRepository(coinRepositoryImpl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(networkRepositoryImpl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindWebSocketRepository(webSocketRepositoryImpl: WebSocketRepositoryImpl): WebSocketRepository
}