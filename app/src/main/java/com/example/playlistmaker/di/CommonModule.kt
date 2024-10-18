package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.common.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}