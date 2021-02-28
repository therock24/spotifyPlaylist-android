package com.example.spotifyplaylist.ui.tracks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyplaylist.models.PlaylistTracksResponse
import com.example.spotifyplaylist.repository.PlaylistRepository
import com.example.spotifyplaylist.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class PlaylistTracksViewModel(
    val playlistTracksRepository: PlaylistRepository
): ViewModel() {

    val playlistsTracksLiveData: MutableLiveData<Resource<PlaylistTracksResponse>> = MutableLiveData()
    val playlistTracksPage = 1
    lateinit var playlistId: String

    fun getPlaylistTracks(countryCode: String) = viewModelScope.launch {
        playlistsTracksLiveData.postValue(Resource.Loading())
        val response = playlistTracksRepository.getPlaylistTracks(countryCode,playlistId,playlistTracksPage)
        playlistsTracksLiveData.postValue(handlePlaylistTracksResponse(response))
    }

    private fun handlePlaylistTracksResponse(response: Response<PlaylistTracksResponse>?): Resource<PlaylistTracksResponse> {

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