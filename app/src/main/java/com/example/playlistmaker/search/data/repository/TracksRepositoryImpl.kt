package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.model.Resourse

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resourse<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        return if (response is TrackSearchResponse) {
            val trackList = response.results.map { trackDto ->
                Track(
                    trackId = trackDto.trackId,
                    trackName = trackDto.trackName,
                    artistName = trackDto.artistName,
                    trackTimeMillis = trackDto.trackTimeMillis,
                    artworkUrl100 = trackDto.artworkUrl100,
                    collectionName = trackDto.collectionName,
                    releaseDate = trackDto.releaseDate,
                    primaryGenreName = trackDto.primaryGenreName,
                    country = trackDto.country,
                    previewUrl = trackDto.previewUrl
                )
            }
            Resourse.Success(trackList)
        } else {
            Resourse.Error(response.resultCode)
        }
    }
}