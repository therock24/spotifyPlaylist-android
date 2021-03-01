package com.example.spotifyplaylist.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.spotifyplaylist.models.PlaylistItem

/**
 * Data access object to obtain playlists from the Room Database.
 */
@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PlaylistItem)

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): LiveData<List<PlaylistItem>>

    @Delete
    suspend fun deletePlaylist(item: PlaylistItem)
}