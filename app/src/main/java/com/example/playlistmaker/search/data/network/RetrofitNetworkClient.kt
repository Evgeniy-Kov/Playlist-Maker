package com.example.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesSearchApi,
    private val context: Context
) : NetworkClient {

//    private val baseUrl = "https://itunes.apple.com"
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(baseUrl)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val iTunesApiService = retrofit.create(ITunesSearchApi::class.java)

    override fun doRequest(dto: Any): Response {
        try {
            if (!isConnected()) {
                return Response(500)
            }
            if (dto is TrackSearchRequest) {
                val response = iTunesApiService.search(dto.expression).execute()

                val body = response.body() ?: Response()

                return body.apply { resultCode = response.code() }
            } else {
                return Response(400)
            }
        } catch (e: Exception) {
            return Response(500)
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