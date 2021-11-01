package com.example.mvvmbithumb.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialogFragment<T : ViewDataBinding>(private val _layoutID: Int) :
    DialogFragment() {
    lateinit var _dataBinding: T

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _dataBinding = DataBindingUtil.inflate(inflater, _layoutID, container, false)
        return _dataBinding.root
    }

    fun show(manager: FragmentManager) {
        show(manager, javaClass.simpleName)
    }
}