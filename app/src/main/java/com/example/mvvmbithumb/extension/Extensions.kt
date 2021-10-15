package com.example.mvvmbithumb.extension

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmbithumb.App
import com.example.mvvmbithumb.viewmodel.ViewModelFactory

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun Activity.getViewModelFactory(): ViewModelFactory {
    val repository = (applicationContext as App).bithumbRepository
    return ViewModelFactory(repository)
}

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as App).bithumbRepository
    return ViewModelFactory(repository)
}


fun Activity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
}