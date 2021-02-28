package com.example.spotifyplaylist.ui.playlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyplaylist.models.PlaylistResponse
import com.example.spotifyplaylist.repository.PlaylistRepository
import com.example.spotifyplaylist.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class PlaylistViewModel(
    val playlistRepository: PlaylistRepository
): ViewModel() {

    val playlistsLiveData: MutableLiveData<Resource<PlaylistResponse>> = MutableLiveData()
    val playlistsPage = 1

    fun getFeaturedPlaylists(countryCode: String) = viewModelScope.launch {
        playlistsLiveData.postValue(Resource.Loading())
        val response = playlistRepository.getFeaturedPlaylists(countryCode,playlistsPage)
        playlistsLiveData.postValue(handlePlaylistsResponse(response))
    }

    private fun handlePlaylistsResponse(response: Response<PlaylistResponse>?): Resource<PlaylistResponse> {

        response?.let {
            if(response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    return Resource.Success(resultResponse)
                }
            }
        }

        return Resource.Error(response?.message())
    }
}