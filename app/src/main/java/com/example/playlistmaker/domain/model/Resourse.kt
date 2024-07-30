package com.example.playlistmaker.domain.model

sealed interface Resourse<T> {
    data class Success<T>(val data: T) : Resourse<T>
    data class Error<T>(val message: Int) : Resourse<T>
}