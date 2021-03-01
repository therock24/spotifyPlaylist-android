package com.example.spotifyplaylist.ui.login

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyplaylist.models.RefreshTokenResponse
import com.example.spotifyplaylist.models.RequestTokenResponse
import com.example.spotifyplaylist.network.RetrofitAccountInstance
import com.example.spotifyplaylist.utils.AppPreferences
import com.example.spotifyplaylist.utils.Constants
import com.example.spotifyplaylist.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * ViewModel for [LoginFragment].
 * Responsible for the business logic, mainly with logging in the user and requesting and refreshing
 * user tokens that give access to the Spotify API and passing the data to the [LoginFragment].
 */
class LoginViewModel : ViewModel() {

    val tokenLiveData: MutableLiveData<Resource<RequestTokenResponse>> = MutableLiveData()

    /**
     * Requests a user token to Spotify Auth API that grants access to the remaining API features,
     * such as browsing playlists and tracks.
     *
     * @param code the authorization or refresh code
     * @param refresh whether it is a refresh or a initial token request
     */
    fun requestToken(code: String, refresh: Boolean) = viewModelScope.launch {

        // post Loading value to trigger Progress Bar on UI
        tokenLiveData.postValue(Resource.Loading())

        // check if it is a refresh or initial request of token
        if (refresh) {
            // try to request a refreshed access token
            val response = RetrofitAccountInstance.api.refreshToken(
                    Constants.TOKEN_GRANT_REFRESH,
                    code
            )
            tokenLiveData.postValue(handleRefreshResponse(response))
        } else {
            // try to request a new access token
            val response = RetrofitAccountInstance.api.requestToken(
                    Constants.TOKEN_GRANT_AUTHORIZATION,
                    code,
                    Constants.SPOTIFY_REDIRECT_URI
            )
            tokenLiveData.postValue(handleTokenResponse(response))
        }
    }

    /**
     * Handles the response from a new token request to the Spotify Auth API.
     */
    @VisibleForTesting
    fun handleTokenResponse(response: Response<RequestTokenResponse>?): Resource<RequestTokenResponse> {

        // check if response is valid
        response?.let {
            if(response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    // store tokens in preferences
                    AppPreferences.refreshCode = resultResponse.refresh_token
                    AppPreferences.userToken = resultResponse.access_token
                    AppPreferences.tokenExpiration = System.currentTimeMillis()/1000L + 3600

                    return Resource.Success(resultResponse)
                }
            }
        }

        return Resource.Error(response?.message())
    }

    /**
     * Handles the response from a refresh token request to the Spotify Auth API.
     */
    private fun handleRefreshResponse(response: Response<RefreshTokenResponse>?): Resource<RequestTokenResponse> {

        // check if response is valid
        response?.let {
            if(response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    // store tokens in preferences
                    AppPreferences.userToken = resultResponse.access_token
                    AppPreferences.tokenExpiration = System.currentTimeMillis()/1000L + 3600

                    // return success (returning dummy data as we are just interested in whether it is a Success or not)
                    // This is to reuse the existing LiveData, but it only supports RequestTokenResponse type.
                    return Resource.Success(RequestTokenResponse("","","",0,""))
                }
            }
        }

        return Resource.Error(response?.message())
    }
}