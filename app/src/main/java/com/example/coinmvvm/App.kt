package com.example.coinmvvm

import android.app.Application
import com.example.coinmvvm.data.local.db.CoinDatabase
import com.example.coinmvvm.data.remote.network.NetworkRepository
import com.example.coinmvvm.data.remote.websocket.WebSocketRepository
import com.example.coinmvvm.data.repository.CoinRepositoryImpl
import com.example.coinmvvm.util.NetworkStateLiveData

class App : Application() {
    lateinit var coinRepository: CoinRepositoryImpl

    lateinit var networkStateLiveData: NetworkStateLiveData

    override fun onCreate() {
        super.onCreate()

        coinRepository = CoinRepositoryImpl(
            WebSocketRepository(),
            NetworkRepository(),
            CoinDatabase.getInstance(applicationContext)
        )

        networkStateLiveData = NetworkStateLiveData(applicationContext)
    }
}