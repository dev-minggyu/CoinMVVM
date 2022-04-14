package com.example.coinmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.ui.fragment.home.HomeViewModel

class ViewModelFactory(
    private val _coinRepository: CoinRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(_coinRepository)
            else -> error("Invalid View Model class")
        }
    } as T
}