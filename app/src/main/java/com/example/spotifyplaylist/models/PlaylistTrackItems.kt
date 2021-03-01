package com.example.spotifyplaylist.models

data class PlaylistTrackArtist(
    val name: String
)

data class PlaylistTrackAlbumImage(
    val url: String,
    val height: Int,
    val width: Int
)

data class PlaylistTrackAlbum(
    val name: String,
    val images: List<PlaylistTrackAlbumImage>
)

data class PlaylistTrack(
    val id: String,
    val name: String,
    val album: PlaylistTrackAlbum,
    val artists: List<PlaylistTrackArtist>,
)

data class PlaylistTrackItem(
    val track: PlaylistTrack
    )

data class PlaylistTracksResponse(
    val items: MutableList<PlaylistTrackItem>,
    val limit: Int,
    val offset: Int,
    val total: Int
)