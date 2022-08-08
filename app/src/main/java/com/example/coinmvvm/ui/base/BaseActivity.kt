package com.example.coinmvvm.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding>(private val layoutID: Int) : AppCompatActivity() {
    private var _dataBinding: T? = null
    val dataBinding get() = _dataBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dataBinding = DataBindingUtil.setContentView(this, layoutID)
    }

    override fun onDestroy() {
        super.onDestroy()
        _dataBinding = null
    }
}