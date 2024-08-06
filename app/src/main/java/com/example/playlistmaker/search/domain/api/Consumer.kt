package com.example.playlistmaker.search.domain.api

fun interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}