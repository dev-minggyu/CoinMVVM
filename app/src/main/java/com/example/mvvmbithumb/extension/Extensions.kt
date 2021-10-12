package com.example.mvvmbithumb.extension

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmbithumb.App
import com.example.mvvmbithumb.viewmodel.ViewModelFactory

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun Activity.getViewModelFactory(): ViewModelFactory {
    return getViewModelFactory(applicationContext)
}

fun Fragment.getViewModelFactory(): ViewModelFactory {
    return getViewModelFactory(requireContext().applicationContext)
}

fun getViewModelFactory(applicationContext: Context): ViewModelFactory {
    val repository = (applicationContext as App).bithumbRepository
    return ViewModelFactory(repository)
}