package com.example.spotifyplaylist.ui.tracks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyplaylist.R;
import com.example.spotifyplaylist.databinding.FragmentPlaylistTracksBinding;
import com.example.spotifyplaylist.MainActivity;
import com.example.spotifyplaylist.models.PlaylistItem;
import com.example.spotifyplaylist.utils.Constants;
import com.example.spotifyplaylist.utils.Resource;

import java.util.List;

/**
 * Fragment that shows the list of tracks for a given Spotify Playlist.
 */
public class PlaylistTracksFragment extends Fragment {

    private FragmentPlaylistTracksBinding binding;
    private PlaylistTracksAdapter playlistTracksAdapter;
    private PlaylistTracksViewModel viewModel;
    private Bundle args;

    // pagination control variables
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isScrolling = false;

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            // set scrolling to true when user is scrolling
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            // obtain current scroll position
            LinearLayoutManager manager = (LinearLayoutManager)binding.rvTracks.getLayoutManager();
            int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
            int visibleItemCount = manager.getChildCount();
            int totalItemCount = manager.getItemCount();

            // determine conditions for pagination
            boolean isNotLoadingAndNotLastPage = !isLoading && !isLastPage;
            boolean isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount;
            boolean isNotAtBeginning = firstVisibleItemPosition >= 0;
            boolean isTotalMoreThanVisible = totalItemCount >= Constants.DEFAULT_PAGE_SIZE;
            boolean shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling;

            // check if a new page should be fetched
            if(shouldPaginate) {
                viewModel.getPlaylistTracks(Constants.DEFAULT_COUNTRY);
                isScrolling = false;
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_tracks, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        viewModel.reset();
        binding.rvTracks.invalidate();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ((MainActivity)requireActivity()).playlistTracksViewModel;

        // obtain the id of the playlist to load in this fragment
        args = getArguments();
        if(args != null) {
            PlaylistItem item = (PlaylistItem)args.getSerializable("playlist");
            viewModel.playlistId = item.getId();

        }

        setupRecyclerView();

        // setup the observer for the playlist tracks response
        viewModel.getPlaylistsTracksLiveData().observe(getViewLifecycleOwner(), response -> {
            if(response instanceof Resource.Success){
                hideProgressBar();
                if (response.getData() != null) {
                    List responseItems = (List)response.getData().getItems();

                    // update the recycler view
                    playlistTracksAdapter.submitItems(responseItems);
                    playlistTracksAdapter.notifyDataSetChanged();

                    // update isLoading for pagination
                    int totalPages = response.getData().getTotal() / Constants.DEFAULT_PAGE_SIZE + 1;
                    isLoading = viewModel.getPlaylistTracksPage() == totalPages;
                }
            }
            else if(response instanceof Resource.Error) {
                hideProgressBar();
                if (response.getMessage() != null) {
                    Toast.makeText(requireActivity(),"Error: " + response.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            else if(response instanceof Resource.Loading) {
                showProgressBar();
            }
        });

        // set search listener
        binding.trackSearch.setOnQueryTextListener(viewModel.getSearchListener(playlistTracksAdapter));

        // get initial page of playlists
        viewModel.getPlaylistTracks(Constants.DEFAULT_COUNTRY);
    }


    /**
     * Sets up the Recycler View that will show the list of tracks.
     */
    private void setupRecyclerView() {
        playlistTracksAdapter = new PlaylistTracksAdapter();
        binding.rvTracks.setAdapter(playlistTracksAdapter);
        binding.rvTracks.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvTracks.addOnScrollListener(this.scrollListener);
    }

    private void hideProgressBar() {
        binding.loading.setVisibility(View.INVISIBLE);
        isLoading = false;
    }

    private void showProgressBar() {
        binding.loading.setVisibility(View.VISIBLE);
        isLoading = true;
    }
}