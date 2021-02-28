package com.example.spotifyplaylist.models

import com.example.spotifyplaylist.utils.Constants

class RequestTokenPost {
    var grant_type: String = Constants.TOKEN_GRANT_TYPE
    var code: String = ""
    var redirect_uri: String = Constants.SPOTIFY_REDIRECT_URI
    var client_id: String = Constants.SPOTIFY_CLIENT_ID
    var client_secret: String = Constants.SPOTIFY_CLIENT_SECRET
}