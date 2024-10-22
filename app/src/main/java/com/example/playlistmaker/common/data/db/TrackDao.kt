package com.example.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM ${DbConfig.FAVOURITE_TRACKS_TABLE_NAME}")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM ${DbConfig.FAVOURITE_TRACKS_TABLE_NAME}")
    suspend fun getTracksIds(): List<Long>

    @Delete()
    suspend fun deleteTrack(track: TrackEntity)
}