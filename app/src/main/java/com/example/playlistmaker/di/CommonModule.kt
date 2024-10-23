package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.common.data.converters.TrackDbConverter
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.db.DbConfig
import com.example.playlistmaker.common.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.common.domain.api.TracksInteractor
import com.example.playlistmaker.common.domain.api.TracksRepository
import com.example.playlistmaker.common.domain.impl.TracksInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, DbConfig.DATABASE_NAME)
            .build()
    }

    factory {
        TrackDbConverter()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get())
    }
}