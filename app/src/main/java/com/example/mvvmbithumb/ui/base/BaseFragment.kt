package com.example.mvvmbithumb.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding>(private val _layoutID: Int) : Fragment() {
    private var _dataBinding: T? = null
    val dataBinding get() = _dataBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _dataBinding = DataBindingUtil.inflate(inflater, _layoutID, container, false)
        return _dataBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBinding = null
    }
}