package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.data.repository.DarkThemeModeRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.settings.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.usecase.GetDarkThemeModeUseCase
import com.example.playlistmaker.settings.domain.usecase.SaveDarkThemeModeUseCase
import com.google.gson.Gson

object Creator {
    private const val APP_PREFERENCES = "playlist_maker_preferences"
    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(provideTracksRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    fun provideSaveDarkThemeModeUseCase(): SaveDarkThemeModeUseCase {
        return SaveDarkThemeModeUseCase(provideDarkThemeModeRepository())
    }

    fun provideGetDarkThemeModeUseCase(): GetDarkThemeModeUseCase {
        return GetDarkThemeModeUseCase(provideDarkThemeModeRepository())
    }

    private fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences(), Gson())
    }

    private fun provideDarkThemeModeRepository(): DarkThemeModeRepository {
        return DarkThemeModeRepositoryImpl(provideSharedPreferences())
    }
}