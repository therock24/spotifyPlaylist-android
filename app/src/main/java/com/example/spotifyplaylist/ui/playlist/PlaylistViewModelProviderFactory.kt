package com.example.spotifyplaylist.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifyplaylist.repository.PlaylistRepository

/**
 * Creates a [PlaylistViewModel] with a [PlaylistRepository] attached to it.
 */
class PlaylistViewModelProviderFactory(
        private val playlistRepository: PlaylistRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlaylistViewModel(playlistRepository) as T
    }
}