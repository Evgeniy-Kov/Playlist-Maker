package com.example.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM ${DbConfig.FAVOURITE_TRACKS_TABLE_NAME}")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM ${DbConfig.FAVOURITE_TRACKS_TABLE_NAME}")
    suspend fun getTracksIds(): List<Long>

    @Query("DELETE FROM ${DbConfig.FAVOURITE_TRACKS_TABLE_NAME} WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)
}