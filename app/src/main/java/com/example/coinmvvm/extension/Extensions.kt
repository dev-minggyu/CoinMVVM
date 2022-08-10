package com.example.coinmvvm.extension

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coinmvvm.util.livedata.MutableSingleLiveData
import com.example.coinmvvm.util.livedata.SingleLiveData
import com.google.android.material.snackbar.Snackbar

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun <T> MutableSingleLiveData<T>.asSingleLiveData() = this as SingleLiveData<T>

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