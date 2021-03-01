package com.example.spotifyplaylist.ui.playlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifyplaylist.R;
import com.example.spotifyplaylist.databinding.FragmentPlaylistBinding;
import com.example.spotifyplaylist.MainActivity;
import com.example.spotifyplaylist.utils.Constants;
import com.example.spotifyplaylist.utils.Resource;

import java.util.List;

/**
 * Fragment that shows the list of Spotify featured playlists for a given country.
 */
public class PlaylistFragment extends Fragment  {

    private FragmentPlaylistBinding binding;
    private PlaylistAdapter playlistAdapter;
    private PlaylistViewModel viewModel;

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
            LinearLayoutManager manager = (LinearLayoutManager)binding.rvPlaylists.getLayoutManager();
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
                viewModel.getFeaturedPlaylists(Constants.DEFAULT_COUNTRY);
                isScrolling = false;
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        viewModel.reset();
        binding.rvPlaylists.invalidate();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ((MainActivity)requireActivity()).playlistViewModel;

        setupRecyclerView();

        // set the listener for when a playlist card is clicked
        playlistAdapter.setOnItemClickListener(playlist -> {
            // put the clicked playlist information the bundle
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlist",playlist);

            // navigate to the playlist tracks fragment
            Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
                    .navigate(R.id.action_playlistFragment_to_playlistTracksFragment,bundle);
            return null;
        });

        // setup the observer for the featured playlists response
        viewModel.getPlaylistsLiveData().observe(getViewLifecycleOwner(), response -> {
            if(response instanceof Resource.Success){
                hideProgressBar();
                if (response.getData() != null) {
                    List responseItems = response.getData().getPlaylists().getItems();

                    // update the recycler view
                    playlistAdapter.submitItems(responseItems);
                    playlistAdapter.notifyDataSetChanged();

                    // update isLoading for pagination
                    int totalPages = response.getData().getPlaylists().getTotal() / Constants.DEFAULT_PAGE_SIZE + 1;
                    isLoading = viewModel.getPlaylistsPage() == totalPages;
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
        binding.playlistSearch.setOnQueryTextListener(viewModel.getSearchListener(playlistAdapter));

        // get initial page of playlists
        viewModel.getFeaturedPlaylists(Constants.DEFAULT_COUNTRY);

    }

    /**
     * Sets up the Recycler View that will show the list of featured playlists.
     */
    private void setupRecyclerView() {
        playlistAdapter = new PlaylistAdapter();
        binding.rvPlaylists.setAdapter(playlistAdapter);
        binding.rvPlaylists.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvPlaylists.addOnScrollListener(this.scrollListener);
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