package com.example.coinmvvm.extension

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coinmvvm.App
import com.example.coinmvvm.util.NetworkStateLiveData
import com.example.coinmvvm.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.Channel

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun Activity.getViewModelFactory(): ViewModelFactory {
    val repository = (applicationContext as App).coinRepository
    return ViewModelFactory(repository)
}

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as App).coinRepository
    return ViewModelFactory(repository)
}

fun DialogFragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as App).coinRepository
    return ViewModelFactory(repository)
}

fun Activity.getNetworkStateLiveData(): NetworkStateLiveData {
    return (applicationContext as App).networkStateLiveData
}

fun Fragment.getNetworkStateLiveData(): NetworkStateLiveData {
    return (requireContext().applicationContext as App).networkStateLiveData
}

fun Activity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
}

fun Activity.showSnackBar(
    text: String,
    buttonName: String? = null,
    clickListener: View.OnClickListener? = null
) {
    val snackBar = Snackbar.make(window.decorView.rootView, text, Snackbar.LENGTH_LONG)
    clickListener?.let {
        snackBar.setAction(buttonName, clickListener)
    }
    snackBar.show()
}

fun Fragment.showSnackBar(
    text: String, buttonName: String? = null,
    clickListener: View.OnClickListener? = null
): Snackbar? {
    var snackBar: Snackbar? = null
    view?.let {
        snackBar = Snackbar.make(it, text, Snackbar.LENGTH_INDEFINITE).apply {
            clickListener?.let {
                setAction(buttonName, clickListener)
            }
            show()
        }
    }
    return snackBar
}

suspend fun <E> Channel<E>.sendIgnoreClosed(event: E) {
    try {
        send(event)
    } catch (e: Exception) {

    }
}