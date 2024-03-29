package com.example.coinmvvm.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.example.coinmvvm.constant.enums.NetworkState
import kotlinx.coroutines.*

class NetworkStateLiveData(context: Context) : LiveData<NetworkState>() {
    private var connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private var _job: Job? = null
    private var _scope = CoroutineScope(Dispatchers.Default)

    private val connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _job?.cancel()
            postValue(NetworkState.CONNECTED)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _job = _scope.launch {
                delay(1000)
                updateState()
            }
        }
    }

    override fun onActive() {
        super.onActive()
        registerNetworkCallback()
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }

    private fun registerNetworkCallback() {
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(),
            connectivityManagerCallback
        )
    }

    fun updateState() {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        ) {
            postValue(NetworkState.CONNECTED)
        } else {
            postValue(NetworkState.DISCONNECTED)
        }
    }
}