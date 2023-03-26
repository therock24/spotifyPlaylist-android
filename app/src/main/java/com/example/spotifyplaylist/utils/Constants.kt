package com.example.spotifyplaylist.utils

/**
 * Constant values used in several places in the code.
 * These values are mainly used for the requests to the Spotify Api.
 */
class Constants {
    companion object {
        // spotify api urls
        const val SPOTIFY_API_URL = "https://api.spotify.com/"
        const val SPOTIFY_ACCOUNTS_URL = "https://accounts.spotify.com/"
        const val SPOTIFY_REDIRECT_URI = "yourcustomprotocol://callback"

        // spotify authentication
        const val SPOTIFY_CLIENT_ID = "<your-client-id>"
        const val SPOTIFY_CLIENT_SECRET = "<your-client-secret>"
        const val SPOTIFY_AUTHORIZATION_BASE64 = "Basic <base-64-code>"

        // authentication grant types for spotify auth
        const val TOKEN_GRANT_AUTHORIZATION = "authorization_code"
        const val TOKEN_GRANT_REFRESH = "refresh_token"

        // query parameters
        const val DEFAULT_PAGE_SIZE = 10
        const val DEFAULT_COUNTRY = "PT"

        // other constants
        const val SLEEP_BEFORE_SEARCH = 500L
    }
}
