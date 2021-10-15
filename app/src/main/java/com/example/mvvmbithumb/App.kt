package com.example.mvvmbithumb

import android.app.Application
import com.example.mvvmbithumb.repository.BithumbRepositoryImpl
import com.example.mvvmbithumb.repository.websocket.WebSocketProvider
import com.example.mvvmbithumb.util.NetworkStateLiveData

class App : Application() {
    val bithumbRepository = BithumbRepositoryImpl(WebSocketProvider())

    val networkStateLiveData: NetworkStateLiveData by lazy {
        NetworkStateLiveData(applicationContext)
    }
}