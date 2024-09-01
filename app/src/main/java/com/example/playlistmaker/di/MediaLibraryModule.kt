package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.FavouriteTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel {
        FavouriteTracksViewModel()
    }
}