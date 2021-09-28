package com.example.mvvmbithumb.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmbithumb.ui.fragment.home.HomeViewModel
import com.example.mvvmbithumb.ui.repository.DataRepository
import com.example.mvvmbithumb.ui.repository.websocket.WSProvider

class ViewModelFactory(
    private val dataRepository: DataRepository,
    private val wsProvider: WSProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(wsProvider)
            else -> error("Invalid View Model class")
        }
    } as T
}