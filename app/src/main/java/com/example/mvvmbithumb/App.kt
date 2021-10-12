package com.example.mvvmbithumb

import android.app.Application
import com.example.mvvmbithumb.repository.BithumbRepository
import com.example.mvvmbithumb.repository.BithumbRepositoryImpl
import com.example.mvvmbithumb.repository.websocket.WebSocketProvider

class App : Application() {
    val bithumbRepository: BithumbRepository = BithumbRepositoryImpl(WebSocketProvider())
}