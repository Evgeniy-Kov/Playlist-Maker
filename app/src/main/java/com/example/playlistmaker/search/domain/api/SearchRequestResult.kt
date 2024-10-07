package com.example.playlistmaker.search.domain.api

sealed interface SearchRequestResult<T> {
    data class Data<T>(val value: T) : SearchRequestResult<T>
    data class Error<T>(val message: Int) : SearchRequestResult<T>
}