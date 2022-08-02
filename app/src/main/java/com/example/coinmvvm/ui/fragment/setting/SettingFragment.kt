package com.example.coinmvvm.ui.fragment.setting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.coinmvvm.R
import com.example.coinmvvm.util.AppThemeManager

class SettingFragment : PreferenceFragmentCompat() {

    private lateinit var _preference: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_settings, rootKey)

        _preference = PreferenceManager.getDefaultSharedPreferences(requireContext())
        _preference.registerOnSharedPreferenceChangeListener { pref, key ->
            when (key) {
                getString(R.string.key_pref_app_theme) -> {
                    val theme = pref.getString(key, AppThemeManager.THEME_SYSTEM)!!
                    AppThemeManager.applyTheme(theme)
                }
            }
        }
    }
}