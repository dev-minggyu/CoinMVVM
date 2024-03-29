package com.example.coinmvvm

import android.app.Application
import androidx.preference.PreferenceManager
import com.example.coinmvvm.util.AppThemeManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        initTheme()
    }

    private fun initTheme() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = pref.getString(getString(R.string.key_pref_app_theme), AppThemeManager.THEME_SYSTEM)!!
        AppThemeManager.applyTheme(theme)
    }

    companion object {
        private lateinit var INSTANCE: App

        fun getString(resID: Int): String {
            return INSTANCE.getString(resID)
        }
    }
}