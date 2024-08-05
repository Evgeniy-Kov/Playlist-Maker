package com.example.playlistmaker.search.ui

data class SearchInputState(
    var hasFocus: Boolean,
    var isTextEmpty: Boolean,
    var isSearchHistoryEmpty: Boolean = true
)