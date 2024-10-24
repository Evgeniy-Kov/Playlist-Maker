package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesSearchApi,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {

        return withContext(Dispatchers.IO){
            try {
                if (!isConnected()) {
                    Response(500)
                }
                if (dto is TrackSearchRequest) {
                    iTunesApiService.search(dto.expression)
                } else {
                    Response(400)
                }
            } catch (e: Exception) {
                Response(500)
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}