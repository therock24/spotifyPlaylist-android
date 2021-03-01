package com.example.spotifyplaylist.ui.playlist

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifyplaylist.models.PlaylistResponse
import com.example.spotifyplaylist.repository.PlaylistRepository
import com.example.spotifyplaylist.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * ViewModel for [PlaylistFragment].
 * Responsible for the business logic, mainly with requesting the featured playlists information
 * and passing the data to the [PlaylistFragment].
 */
class PlaylistViewModel(
        private val playlistRepository: PlaylistRepository
): ViewModel() {

    val playlistsLiveData: MutableLiveData<Resource<PlaylistResponse>> = MutableLiveData()
    var playlistsPage = 1
    var playlistResponse: PlaylistResponse? = null
    var searchJob: Job? = null

    /**
     * Resets pagination and responses when switching views.
     */
    fun reset() {
        playlistsPage = 1
        playlistResponse = null
    }

    /**
     * This method issues a request to the Spotify API to obtain the featured playlists for the given
     * country, including the current page (for pagination support).
     */
    fun getFeaturedPlaylists(countryCode: String) = viewModelScope.launch {

        // issue loading state to fragment
        playlistsLiveData.postValue(Resource.Loading())

        // request playlists and post result
        val response = playlistRepository.getFeaturedPlaylists(countryCode, playlistsPage)
        playlistsLiveData.postValue(handlePlaylistsResponse(response))
    }

    /**
     * Provides the listener for searching for a playlist.
     */
    fun getSearchListener(playlistAdapter: PlaylistAdapter): SearchView.OnQueryTextListener {
        return object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // wait some time before filtering the list with the search input
                searchJob?.cancel()
                searchJob = MainScope().launch {
                    delay(500L)
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
    private fun handlePlaylistsResponse(response: Response<PlaylistResponse>?): Resource<PlaylistResponse> {

        response?.let {
            if(response.isSuccessful) {
                response.body()?.let { resultResponse ->
                    // increment page number
                    playlistsPage++

                    // check if it is the first response
                    if(playlistResponse == null) {
                        playlistResponse = resultResponse
                    } else {
                        val oldPlaylists = playlistResponse?.playlists?.items
                        val newPlaylists = resultResponse.playlists.items
                        oldPlaylists?.addAll(newPlaylists)
                    }
                    return Resource.Success(playlistResponse ?: resultResponse)
                }
            }
        }

        return Resource.Error(response?.message())
    }



}