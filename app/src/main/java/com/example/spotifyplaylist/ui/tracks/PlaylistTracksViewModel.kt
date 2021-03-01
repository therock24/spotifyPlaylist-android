package com.example.spotifyplaylist.ui.tracks

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyplaylist.models.PlaylistTracksResponse
import com.example.spotifyplaylist.repository.PlaylistRepository
import com.example.spotifyplaylist.utils.Constants
import com.example.spotifyplaylist.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * ViewModel for [PlaylistTracksFragment].
 * Responsible for the business logic, mainly with requesting the track information for a given
 * playlist and passing the data to the [PlaylistTracksFragment].
 */
class PlaylistTracksViewModel(val playlistTracksRepository: PlaylistRepository): ViewModel() {

    val playlistsTracksLiveData: MutableLiveData<Resource<PlaylistTracksResponse>> = MutableLiveData()
    var playlistTracksPage = 1
    lateinit var playlistId: String
    var playlistTracksResponse: PlaylistTracksResponse? = null
    var searchJob: Job? = null

    /**
     * Resets pagination and responses when switching views.
     */
    fun reset() {
        playlistTracksPage = 1
        playlistTracksResponse = null
    }

    /**
     * This method issues a request to the Spotify API to obtain the tracks for a given playlist,
     * including the current page (for pagination support).
     */
    fun getPlaylistTracks(countryCode: String) = viewModelScope.launch {

        // issue loading state to fragment
        playlistsTracksLiveData.postValue(Resource.Loading())

        // request tracks and post result
        val response = playlistTracksRepository.getPlaylistTracks(countryCode, playlistId, playlistTracksPage)
        playlistsTracksLiveData.postValue(handlePlaylistTracksResponse(response))
    }

    /**
     * Provides the listener for searching a track on the playlist tracks list.
     */
    fun getSearchListener(playlistAdapter: PlaylistTracksAdapter): SearchView.OnQueryTextListener {
        return object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                // wait some time before filtering the list with the search input
                searchJob?.cancel()
                searchJob = MainScope().launch {
                    delay(Constants.SLEEP_BEFORE_SEARCH)
                }

                // filter values on the adapter
                playlistAdapter.filter.filter(newText)
                return false
            }
        }
    }

    /**
     * Handle the response from the Spotify Api and add new items to the list.
     */
    private fun handlePlaylistTracksResponse(response: Response<PlaylistTracksResponse>?): Resource<PlaylistTracksResponse> {

        response?.let {
            if(response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    // increment page number
                    playlistTracksPage++

                    // check if it is the first response
                    if(playlistTracksResponse == null) {
                        playlistTracksResponse = resultResponse
                    } else {
                        val oldTracks = playlistTracksResponse?.items
                        val newTracks = resultResponse.items
                        oldTracks?.addAll(newTracks)
                    }

                    return Resource.Success(playlistTracksResponse ?: resultResponse)
                }
            }
        }

        return Resource.Error(response?.message())
    }
}