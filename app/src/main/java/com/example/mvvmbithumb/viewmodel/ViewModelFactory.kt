package com.example.mvvmbithumb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmbithumb.data.repository.BithumbRepository
import com.example.mvvmbithumb.ui.fragment.home.HomeViewModel

class ViewModelFactory(
    private val _bithumbRepository: BithumbRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(_bithumbRepository)
            else -> error("Invalid View Model class")
        }
    } as T
}