package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.favourite.FavouriteTracksViewModel
import com.example.playlistmaker.library.ui.playlists.NewPlaylistViewModel
import com.example.playlistmaker.library.ui.playlists.PlaylistViewModel
import com.example.playlistmaker.library.ui.playlists.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaLibraryModule = module {

    viewModel {
        FavouriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }
}