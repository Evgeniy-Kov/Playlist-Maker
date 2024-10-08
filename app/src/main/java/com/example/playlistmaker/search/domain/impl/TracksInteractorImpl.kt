package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.api.Consumer
import com.example.playlistmaker.search.domain.api.ConsumerData
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Resourse
import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: Consumer<List<Track>>) {
        executor.execute {

            when (val response = tracksRepository.searchTracks(expression)) {
                is Resourse.Success -> {
                    consumer.consume(ConsumerData.Data(response.data))
                }

                is Resourse.Error -> {
                    consumer.consume(ConsumerData.Error(response.message))
                }
            }
        }
    }
}