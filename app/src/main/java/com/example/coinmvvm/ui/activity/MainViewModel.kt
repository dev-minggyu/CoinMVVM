package com.example.coinmvvm.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coinmvvm.R
import com.example.coinmvvm.extension.asLiveData

class MainViewModel : ViewModel() {
    private val _currentMenuId: MutableLiveData<Int> = MutableLiveData(R.id.home_fragment)
    val currentMenuId = _currentMenuId.asLiveData()

    val selectedNavigationItem = { itemId: Int ->
        _currentMenuId.value = itemId
    }
}