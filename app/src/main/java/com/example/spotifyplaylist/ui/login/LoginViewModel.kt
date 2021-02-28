package com.example.spotifyplaylist.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyplaylist.models.RequestTokenResponse
import com.example.spotifyplaylist.network.RetrofitAccountInstance
import com.example.spotifyplaylist.utils.Constants
import com.example.spotifyplaylist.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import android.util.Log
import com.example.spotifyplaylist.utils.AppPreferences


class LoginViewModel(): ViewModel() {

    val loginLiveData: MutableLiveData<Resource<RequestTokenResponse>> = MutableLiveData()

    fun requestToken(authorization: String) = viewModelScope.launch {
        loginLiveData.postValue(Resource.Loading())

        val response = AppPreferences.refreshCode?.let { code ->
            RetrofitAccountInstance.api.requestToken(
                Constants.TOKEN_GRANT_TYPE,
                code,
                Constants.SPOTIFY_REDIRECT_URI
            )
        }
        loginLiveData.postValue(handleTokenResponse(response))
    }

    private fun handleTokenResponse(response: Response<RequestTokenResponse>?): Resource<RequestTokenResponse> {

        response?.let {
            if(response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    AppPreferences.refreshCode = resultResponse.refresh_token
                    AppPreferences.userToken = resultResponse.access_token
                    Log.i("LoginViewModel", "Got a userToken ${resultResponse.access_token}")
                    return Resource.Success(resultResponse)
                }
            }
        }

        return Resource.Error(response?.message())
    }
}