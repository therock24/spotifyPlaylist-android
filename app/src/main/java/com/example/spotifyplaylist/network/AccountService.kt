package com.example.spotifyplaylist.network

import com.example.spotifyplaylist.models.RefreshTokenResponse
import com.example.spotifyplaylist.models.RequestTokenResponse
import com.example.spotifyplaylist.utils.Constants
import retrofit2.Response
import retrofit2.http.*

interface AccountService {

    @Headers("Authorization: ${Constants.SPOTIFY_AUTHORIZATION_BASE64}",
        "Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("api/token")
    suspend fun requestToken(@Field("grant_type") grant_type : String,
                             @Field("code") code : String,
                             @Field("redirect_uri") redirect_uri : String
    ): Response<RequestTokenResponse>

    @Headers("Authorization: ${Constants.SPOTIFY_AUTHORIZATION_BASE64}",
        "Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("api/token")
    suspend fun refreshToken(@Field("grant_type") grant_type : String,
                             @Field("refresh_token") refresh_token : String
    ): Response<RefreshTokenResponse>

}


