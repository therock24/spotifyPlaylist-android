package com.example.spotifyplaylist.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyplaylist.repository.PlaylistRepository

class PlaylistViewModelProviderFactory(
    val playlistRepository: PlaylistRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlaylistViewModel(playlistRepository) as T
    }
}