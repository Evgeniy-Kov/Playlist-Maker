package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.LocalResourcesRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.LocalResourcesRepository
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module {

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory<LocalResourcesRepository> {
        LocalResourcesRepositoryImpl(androidContext())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }
}