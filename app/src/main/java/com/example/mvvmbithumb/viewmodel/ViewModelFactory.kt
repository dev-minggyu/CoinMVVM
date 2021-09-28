package com.example.mvvmbithumb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmbithumb.ui.fragment.home.HomeViewModel
import com.example.mvvmbithumb.repository.DataManager

class ViewModelFactory(
    private val _dataManager: DataManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(_dataManager)
            else -> error("Invalid View Model class")
        }
    } as T
}