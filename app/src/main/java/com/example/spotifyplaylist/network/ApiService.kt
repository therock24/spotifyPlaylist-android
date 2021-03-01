package com.example.spotifyplaylist.network


import com.example.spotifyplaylist.models.PlaylistResponse
import com.example.spotifyplaylist.models.PlaylistTracksResponse
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This service contains the Spotify API requests regarding playlists and tracks.
 */
interface ApiService {

    @GET("v1/browse/featured-playlists/")
    suspend fun getFeaturedPlaylists(@Header("Authorization") token: String,
                             @Query("country") country: String,
                             @Query("limit") per_page: Int,
                             @Query("offset") offset: Int): Response<PlaylistResponse>

    @GET("v1/playlists/{playlist_id}/tracks/")
    suspend fun getPlaylistTracks(@Header("Authorization") token: String,
                          @Path("playlist_id") playlist_id: String,
                          @Query("market") country: String,
                          @Query("limit") per_page: Int,
                          @Query("offset") offset: Int): Response<PlaylistTracksResponse>
}

