package com.example.coinmvvm.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coinmvvm.R
import com.example.coinmvvm.data.repository.CoinRepository
import com.example.coinmvvm.extension.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val _coinRepository: CoinRepository
) : ViewModel() {

    private val _currentMenuId: MutableLiveData<Int> = MutableLiveData(R.id.home_fragment)
    val currentMenuId = _currentMenuId.asLiveData()

    val selectedNavigationItem = { itemId: Int ->
        _currentMenuId.value = itemId
    }

    fun listenTickerPrice() {
        viewModelScope.launch {
            _coinRepository.listenTickerSocket()
        }
    }

    fun stopTickerSocket() {
        viewModelScope.launch {
            _coinRepository.stopTickerSocket()
        }
    }

    fun retryListenPrice() {
        viewModelScope.launch {
            _coinRepository.stopTickerSocket()
            _coinRepository.listenTickerSocket()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTickerSocket()
    }
}