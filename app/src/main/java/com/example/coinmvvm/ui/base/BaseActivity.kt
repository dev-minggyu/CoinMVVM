package com.example.coinmvvm.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.coinmvvm.util.NetworkStateLiveData
import javax.inject.Inject

abstract class BaseActivity<T : ViewDataBinding>(private val layoutID: Int) : AppCompatActivity() {
    private var _dataBinding: T? = null
    val dataBinding get() = _dataBinding!!

    @Inject
    lateinit var networkStateLiveData: NetworkStateLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dataBinding = DataBindingUtil.setContentView<T>(this, layoutID).apply {
            lifecycleOwner = this@BaseActivity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _dataBinding = null
    }
}