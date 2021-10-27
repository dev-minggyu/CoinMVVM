package com.example.mvvmbithumb

import android.app.Application
import com.example.mvvmbithumb.data.network.NetworkRepository
import com.example.mvvmbithumb.data.repository.BithumbRepositoryImpl
import com.example.mvvmbithumb.data.websocket.WebSocketProvider
import com.example.mvvmbithumb.util.NetworkStateLiveData

class App : Application() {
    val bithumbRepository = BithumbRepositoryImpl(WebSocketProvider(), NetworkRepository())

    val networkStateLiveData: NetworkStateLiveData by lazy {
        NetworkStateLiveData(applicationContext)
    }
}