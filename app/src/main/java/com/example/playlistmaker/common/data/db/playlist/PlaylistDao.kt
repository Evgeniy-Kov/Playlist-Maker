package com.example.playlistmaker.common.data.db.playlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.playlistmaker.common.data.db.DbConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlistEntity: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToDb(track: PlaylistTrackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlaylistTrackCrossRef(model: PlaylistTrackCrossRef)

    @Query("SELECT * FROM ${DbConfig.PLAYLISTS_TABLE_NAME}")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Transaction
    @Query("SELECT * FROM ${DbConfig.PLAYLISTS_TABLE_NAME} WHERE playlistId = :playlistId")
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracksDto>

    @Transaction
    @Query("SELECT * FROM ${DbConfig.PLAYLIST_TRACK_TABLE_NAME} WHERE trackId = :trackId")
    fun getTrackWithPlaylists(trackId: Long): Flow<TrackWithPlaylistsDto>
}