package com.example.coinmvvm

import android.app.Application
import com.example.coinmvvm.util.NetworkStateLiveData
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    lateinit var networkStateLiveData: NetworkStateLiveData

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        networkStateLiveData = NetworkStateLiveData(applicationContext)
    }

    companion object {
        private lateinit var INSTANCE: App

        fun getString(resID: Int): String {
            return INSTANCE.getString(resID)
        }
    }
}