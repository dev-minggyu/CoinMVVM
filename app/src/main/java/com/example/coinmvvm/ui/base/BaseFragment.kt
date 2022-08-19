package com.example.coinmvvm.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.coinmvvm.util.NetworkStateLiveData
import javax.inject.Inject

abstract class BaseFragment<T : ViewDataBinding>(private val layoutID: Int) : Fragment() {
    private var _dataBinding: T? = null
    val dataBinding get() = _dataBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _dataBinding = DataBindingUtil.inflate<T>(inflater, layoutID, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return _dataBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBinding = null
    }
}