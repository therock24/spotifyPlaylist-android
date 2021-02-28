package com.example.spotifyplaylist.repository

import com.example.spotifyplaylist.db.PlaylistDatabase
import com.example.spotifyplaylist.network.RetrofitApiInstance
import com.example.spotifyplaylist.utils.AppPreferences
import com.example.spotifyplaylist.utils.Constants.Companion.DEFAULT_PAGE_SIZE

class PlaylistRepository(
    val db: PlaylistDatabase
) {

    suspend fun getFeaturedPlaylists(countryCode: String, pageNumber: Int) =
        AppPreferences.userToken?.let { token ->
            RetrofitApiInstance.api.getFeaturedPlaylists(
                "Bearer $token",
                countryCode,
                DEFAULT_PAGE_SIZE,
                pageNumber*DEFAULT_PAGE_SIZE-1)
        }

    suspend fun getPlaylistTracks(countryCode: String, playlistId: String, pageNumber: Int) =
        AppPreferences.userToken?.let { token ->
            RetrofitApiInstance.api.getPlaylistTracks("Bearer $token",
                playlistId,
                countryCode,
                DEFAULT_PAGE_SIZE,
                pageNumber*DEFAULT_PAGE_SIZE-1
            )
        }
}