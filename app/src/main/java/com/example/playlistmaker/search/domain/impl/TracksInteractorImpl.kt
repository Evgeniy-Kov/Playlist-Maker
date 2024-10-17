package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.api.SearchRequestResult
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Resourse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<SearchRequestResult<List<Track>>> {
        return tracksRepository.searchTracks(expression).map { result ->
            when (result) {
                is Resourse.Success -> {
                    SearchRequestResult.Data(result.data)
                }

                is Resourse.Error -> {
                    SearchRequestResult.Error(result.message)
                }
            }
        }
    }
}