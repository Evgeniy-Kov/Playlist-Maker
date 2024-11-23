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

    @Query("SELECT * FROM ${DbConfig.PLAYLISTS_TABLE_NAME} WHERE playlistId = :playlistId")
    fun getPlaylist(playlistId: Long): PlaylistEntity

    @Query("SELECT * FROM ${DbConfig.PLAYLISTS_TABLE_NAME}")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT trackId FROM ${DbConfig.PLAYLIST_TRACK_CROSS_REF_TABLE_NAME} " +
            "WHERE playlistId = :playlistId")
    suspend fun getTracksIds(playlistId: Long): List<Long>

    @Query("SELECT EXISTS(SELECT * FROM  ${DbConfig.PLAYLIST_TRACK_CROSS_REF_TABLE_NAME} " +
            "WHERE trackId = :trackId)")
    suspend fun isTrackExistsInAnotherPlaylists(trackId: Long): Boolean

    @Query("DELETE FROM ${DbConfig.PLAYLISTS_TABLE_NAME} WHERE playlistId = :playlistId")
    suspend fun deletePlaylistFromDb(playlistId: Long)

    @Query("DELETE FROM ${DbConfig.PLAYLIST_TRACK_TABLE_NAME} WHERE trackId = :trackId")
    suspend fun deleteTrackFromDb(trackId: Long)

    @Query("DELETE FROM ${DbConfig.PLAYLIST_TRACK_CROSS_REF_TABLE_NAME} " +
            "WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deletePlaylistTrackCrossRef(playlistId: Long, trackId: Long)

    @Query("DELETE FROM ${DbConfig.PLAYLIST_TRACK_CROSS_REF_TABLE_NAME} " +
            "WHERE playlistId = :playlistId")
    suspend fun deletePlaylistTrackCrossRef(playlistId: Long)

    @Transaction
    suspend fun deletePlaylist(playlistId: Long) {
        val tracksIds = getTracksIds(playlistId)
        deletePlaylistFromDb(playlistId)
        deletePlaylistTrackCrossRef(playlistId)
        for (id in tracksIds) {
            val isTrackHasRef = isTrackExistsInAnotherPlaylists(id)

            if (!isTrackHasRef) {
                deleteTrackFromDb(id)
            }
        }
    }

    @Transaction
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        deletePlaylistTrackCrossRef(playlistId, trackId)
        val isTrackHasRef = isTrackExistsInAnotherPlaylists(trackId)
        if (!isTrackHasRef) {
            deleteTrackFromDb(trackId)
        }
        val playlist = getPlaylist(playlistId)
        addPlaylist(playlist.copy(tracksCount = playlist.tracksCount -1))
    }

    @Transaction
    @Query("SELECT * FROM ${DbConfig.PLAYLISTS_TABLE_NAME} WHERE playlistId = :playlistId")
    fun getPlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracksDto>

    @Transaction
    @Query("SELECT * FROM ${DbConfig.PLAYLIST_TRACK_TABLE_NAME} WHERE trackId = :trackId")
    fun getTrackWithPlaylists(trackId: Long): Flow<TrackWithPlaylistsDto>
}