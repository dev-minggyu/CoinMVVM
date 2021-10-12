package com.example.mvvmbithumb.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding>(val layoutID: Int) : AppCompatActivity() {
    lateinit var _dataBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dataBinding = DataBindingUtil.setContentView(this, layoutID)
    }
}