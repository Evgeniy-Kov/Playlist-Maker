package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.api.ConsumerData
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable by lazy {
        Runnable {
            val newSearchText = lastSearchText
            sendQuery(newSearchText)
        }
    }

    private var lastSearchText = ""

    private val trackList = mutableListOf<Track>()

    private val _screenStateLiveData = MutableLiveData<SearchFragmentState>()

    val screenStateLiveData: LiveData<SearchFragmentState>
        get() = _screenStateLiveData

    private val _isClearInputbuttonVisibileLiveData = MutableLiveData<Boolean>(false)
    val isClearInputbuttonVisibileLiveData: LiveData<Boolean>
        get() = _isClearInputbuttonVisibileLiveData

    fun searchDebounce(changedText: String) {
        lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    fun onInputStateChanged(hasFocus: Boolean, searchInput: CharSequence?) {
        val searchHistory = searchHistoryInteractor.getSearchHistory()
        _isClearInputbuttonVisibileLiveData.value = searchInput.toString().isNotEmpty()
        if (hasFocus && searchInput.toString().isEmpty() && searchHistory.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
            _screenStateLiveData.value = SearchFragmentState.History(searchHistory)
        } else {
            searchDebounce(searchInput.toString())
        }
    }

    fun changeScreenState(state: SearchFragmentState) {
        _screenStateLiveData.value = state
    }

    fun cleanSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
        _screenStateLiveData.value = SearchFragmentState.Content(emptyList())
    }

    fun repeatLastRequest() {
        sendQuery(lastSearchText)
    }

    fun addTrackToSearchHistory(track: Track) {
        searchHistoryInteractor.addTrackToSearchHistory(track)
        if (_screenStateLiveData.value is SearchFragmentState.History) {
            _screenStateLiveData.value =
                SearchFragmentState.History(searchHistoryInteractor.getSearchHistory())
        }
    }

    private fun sendQuery(newSearchText: String) {
        if (newSearchText.isNotBlank()) {
            _screenStateLiveData.value = SearchFragmentState.Loading
            tracksInteractor.searchTracks(
                expression = newSearchText,
                consumer = { data ->
                    if (screenStateLiveData.value != SearchFragmentState.Loading) {
                        return@searchTracks
                    }
                    when (data) {
                        is ConsumerData.Data -> {
                            if (!data.value.isNullOrEmpty()) {
                                trackList.clear()
                                trackList.addAll(data.value)
                                _screenStateLiveData.postValue(SearchFragmentState.Content(trackList))
                            } else {
                                _screenStateLiveData.postValue(SearchFragmentState.Empty)
                            }
                        }

                        is ConsumerData.Error -> {
                            _screenStateLiveData.postValue(SearchFragmentState.Error)
                        }
                    }
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}