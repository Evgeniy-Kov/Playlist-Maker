package com.example.playlistmaker.library.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.api.TracksInteractor
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val _screenStateLiveData = MutableLiveData<FavouriteTracksFragmentState>()
    val screenStateLiveData: LiveData<FavouriteTracksFragmentState>
        get() = _screenStateLiveData

    init {
        getFavouriteTracks()
    }

    fun getFavouriteTracks() {
        viewModelScope.launch {
            tracksInteractor.getTracks()
                .collect { favouriteTracks ->
                    processResult(favouriteTracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _screenStateLiveData.value = FavouriteTracksFragmentState.Empty
        } else {
            _screenStateLiveData.value = FavouriteTracksFragmentState.Content(tracks)
        }
    }

}