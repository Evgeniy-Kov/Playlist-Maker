package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.api.SearchRequestResult
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val searchDebounce: (String) -> Unit = debounce(
        SEARCH_DEBOUNCE_DELAY_MILLIS,
        viewModelScope,
        true
    ) {
        lastSearchText = it
        if (it.isNotBlank())
            sendQuery(it)
    }

    private var lastSearchText = ""

    private val trackList = mutableListOf<Track>()

    private val _screenStateLiveData = MutableLiveData<SearchFragmentState>()

    val screenStateLiveData: LiveData<SearchFragmentState>
        get() = _screenStateLiveData

    private val _isClearInputbuttonVisibileLiveData = MutableLiveData<Boolean>(false)
    val isClearInputbuttonVisibileLiveData: LiveData<Boolean>
        get() = _isClearInputbuttonVisibileLiveData

    fun onInputStateChanged(hasFocus: Boolean, searchInput: CharSequence?) {
        val searchHistory = searchHistoryInteractor.getSearchHistory()
        _isClearInputbuttonVisibileLiveData.value = searchInput.toString().isNotEmpty()
        if (hasFocus && searchInput.toString().isEmpty() && searchHistory.isNotEmpty()) {
            searchDebounce(searchInput.toString())
            _screenStateLiveData.value = SearchFragmentState.History(searchHistory)
        } else {
            searchDebounce(searchInput.toString())
        }
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
            viewModelScope.launch {
                tracksInteractor.searchTracks(newSearchText)
                    .collect { result ->
                        processResult(result)
                    }
            }
        }
    }

    private fun processResult(result: SearchRequestResult<List<Track>>) {
        if (screenStateLiveData.value != SearchFragmentState.Loading) {
            return
        }
        when (result) {
            is SearchRequestResult.Data<List<Track>> -> {
                if (!result.value.isNullOrEmpty()) {
                    trackList.clear()
                    trackList.addAll(result.value)
                    _screenStateLiveData.postValue(SearchFragmentState.Content(trackList))
                } else {
                    _screenStateLiveData.postValue(SearchFragmentState.Empty)
                }
            }

            is SearchRequestResult.Error -> {
                _screenStateLiveData.postValue(SearchFragmentState.Error)
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}