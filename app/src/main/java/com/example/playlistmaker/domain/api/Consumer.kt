package com.example.playlistmaker.domain.api

fun interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}