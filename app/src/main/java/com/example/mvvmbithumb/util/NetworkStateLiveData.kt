package com.example.mvvmbithumb.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.example.mvvmbithumb.constant.enums.NetworkState

class NetworkStateLiveData(context: Context) : LiveData<NetworkState>() {
    private var connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkRequestBuilder: NetworkRequest.Builder = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

    private val connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(NetworkState.CONNECTED)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(NetworkState.DISCONNECTED)
        }
    }

    override fun onActive() {
        super.onActive()
        updateState()
        registerNetworkCallback()
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }

    private fun registerNetworkCallback() {
        connectivityManager.registerNetworkCallback(
            networkRequestBuilder.build(),
            connectivityManagerCallback
        )
    }

    fun updateState() {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities == null) {
            postValue(NetworkState.DISCONNECTED)
            return
        }

        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            postValue(NetworkState.CONNECTED)
        }
    }
}