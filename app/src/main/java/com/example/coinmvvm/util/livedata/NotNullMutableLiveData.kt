package com.example.coinmvvm.util.livedata

import androidx.lifecycle.MutableLiveData

class NotNullMutableLiveData<T>(defaultValue: T): MutableLiveData<T>(defaultValue) {
    override fun getValue(): T {
        return super.getValue()!!
    }
}