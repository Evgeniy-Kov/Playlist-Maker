package com.example.playlistmaker.common.data.db.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.common.data.db.DbConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlistEntity: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToDb(track: PlaylistTrackEntity)

    @Query("SELECT * FROM ${DbConfig.PLAYLISTS_TABLE_NAME} WHERE playlistId = :playlistId")
    fun getPlaylist(playlistId: Long): Flow<PlaylistEntity>

    @Query("SELECT * FROM ${DbConfig.PLAYLISTS_TABLE_NAME}")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM ${DbConfig.PLAYLIST_TRACK_TABLE_NAME} WHERE trackId IN (:trackIds)")
    fun getTracksByIds(trackIds: List<Long>): Flow<List<PlaylistTrackEntity>>

    @Query("SELECT * FROM ${DbConfig.PLAYLIST_TRACK_TABLE_NAME} WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity

    @Query("DELETE FROM ${DbConfig.PLAYLISTS_TABLE_NAME} WHERE playlistId = :playlistId")
    suspend fun deletePlaylistFromDb(playlistId: Long)

    @Query("DELETE FROM ${DbConfig.PLAYLIST_TRACK_TABLE_NAME} WHERE trackId = :trackId")
    suspend fun deleteTrackFromDb(trackId: Long)

}