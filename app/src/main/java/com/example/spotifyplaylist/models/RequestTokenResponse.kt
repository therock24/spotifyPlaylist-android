package com.example.spotifyplaylist.models

data class RequestTokenResponse (
    var access_token: String,
    var token_type: String,
    var scope: String,
    var expires_in: Int,
    var refresh_token: String
    )