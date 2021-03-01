package com.example.spotifyplaylist.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class PlaylistImage (
    var url: String,
    var height: Int,
    var width: Int
    ) : Serializable

@Entity(tableName = "playlists")
data class PlaylistItem (
    @PrimaryKey
    val id: String,
    val name: String?,
    val images: List<PlaylistImage>?
) : Serializable

data class PlaylistItems(
    val items: MutableList<PlaylistItem>,
    val limit: Int,
    val offset: Int,
    val total: Int,
)

data class PlaylistResponse(
    val playlists: PlaylistItems,
)