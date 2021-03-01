package com.example.spotifyplaylist.repository

import com.example.spotifyplaylist.db.PlaylistDatabase
import com.example.spotifyplaylist.models.PlaylistResponse
import com.example.spotifyplaylist.models.PlaylistTracksResponse
import com.example.spotifyplaylist.network.RetrofitApiInstance
import com.example.spotifyplaylist.utils.AppPreferences
import com.example.spotifyplaylist.utils.Constants.Companion.DEFAULT_PAGE_SIZE
import retrofit2.Response

class PlaylistRepository(val db: PlaylistDatabase) {

    /**
     * Request the featured playlists from the Spotify API, including  pagination with the page
     * number.
     *
     * @param countryCode ISO 3166-1 alpha-2 country code
     * @param pageNumber the page to retrieve
     *
     * @return a set of playlists
     */
    suspend fun getFeaturedPlaylists(countryCode: String, pageNumber: Int):
            Response<PlaylistResponse>? {

        // calculate offset
        val offset = if (pageNumber == 1) {
            0
        } else {
            (pageNumber-1)*DEFAULT_PAGE_SIZE
        }

        return AppPreferences.userToken?.let { token ->
            RetrofitApiInstance.api.getFeaturedPlaylists(
                    "Bearer $token",
                    countryCode,
                    DEFAULT_PAGE_SIZE,
                    offset)
        }
    }

    /**
     * Request the tracks of a give playlist from the Spotify API, including pagination with the
     * page number.
     *
     * @param countryCode ISO 3166-1 alpha-2 country code
     * @param pageNumber the page to retrieve
     * @param playlistId the playlist identifier
     *
     * @return a set of playlists
     */
    suspend fun getPlaylistTracks(countryCode: String, playlistId: String, pageNumber: Int):
            Response<PlaylistTracksResponse>? {

        // calculate offset
        val offset = if (pageNumber == 1) {
            0
        } else {
            (pageNumber-1)*DEFAULT_PAGE_SIZE
        }

        return AppPreferences.userToken?.let { token ->
            RetrofitApiInstance.api.getPlaylistTracks("Bearer $token",
                    playlistId,
                    countryCode,
                    DEFAULT_PAGE_SIZE,
                    offset
            )
        }
    }
}