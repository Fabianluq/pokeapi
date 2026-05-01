package com.example.pokeapi.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkConnectivityObserver(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<Status> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(Status.Available)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                trySend(Status.Losing)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(Status.Lost)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(Status.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
        
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork == null) {
            trySend(Status.Unavailable)
        } else {
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            val isAvailable = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            if (isAvailable) trySend(Status.Available) else trySend(Status.Unavailable)
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}
