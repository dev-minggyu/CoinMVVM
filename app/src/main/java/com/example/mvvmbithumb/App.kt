package com.example.mvvmbithumb

import android.app.Application
import com.example.mvvmbithumb.data.local.db.BithumbDatabase
import com.example.mvvmbithumb.data.network.NetworkRepository
import com.example.mvvmbithumb.data.repository.BithumbRepositoryImpl
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import com.example.mvvmbithumb.util.NetworkStateLiveData

class App : Application() {
    lateinit var bithumbRepository: BithumbRepositoryImpl

    lateinit var networkStateLiveData: NetworkStateLiveData

    override fun onCreate() {
        super.onCreate()

        bithumbRepository = BithumbRepositoryImpl(
            WebSocketProvider(),
            NetworkRepository(),
            BithumbDatabase.getInstance(applicationContext)
        )

        networkStateLiveData = NetworkStateLiveData(applicationContext)
    }
}