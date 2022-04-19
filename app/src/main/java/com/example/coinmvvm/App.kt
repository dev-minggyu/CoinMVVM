package com.example.coinmvvm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
    }

    companion object {
        private lateinit var INSTANCE: App

        fun getString(resID: Int): String {
            return INSTANCE.getString(resID)
        }
    }
}