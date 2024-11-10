package com.example.playlistmaker.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.common.data.db.playlist.PlaylistDao
import com.example.playlistmaker.common.data.db.playlist.PlaylistEntity
import com.example.playlistmaker.common.data.db.playlist.PlaylistTrackCrossRef
import com.example.playlistmaker.common.data.db.playlist.PlaylistTrackEntity

@Database(
    version = 1,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        PlaylistTrackCrossRef::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favouriteTrackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

}