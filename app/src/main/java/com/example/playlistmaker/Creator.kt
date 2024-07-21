package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.reposirtory.DarkThemeModeRepositoryImpl
import com.example.playlistmaker.data.reposirtory.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.reposirtory.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.DarkThemeModeRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.usecase.GetDarkThemeModeUseCase
import com.example.playlistmaker.domain.usecase.SaveDarkThemeModeUseCase

object Creator {
    private const val APP_PREFERENCES = "playlist_maker_preferences"
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(provideTracksRepository())
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    private fun provideDarkThemeModeRepository(): DarkThemeModeRepository {
        return DarkThemeModeRepositoryImpl(provideSharedPreferences())
    }

    fun provideSaveDarkThemeModeUseCase(): SaveDarkThemeModeUseCase {
        return SaveDarkThemeModeUseCase(provideDarkThemeModeRepository())
    }

    fun provideGetDarkThemeModeUseCase(): GetDarkThemeModeUseCase {
        return GetDarkThemeModeUseCase(provideDarkThemeModeRepository())
    }
}