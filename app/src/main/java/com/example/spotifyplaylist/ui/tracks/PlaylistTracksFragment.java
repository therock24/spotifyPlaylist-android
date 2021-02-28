package com.example.spotifyplaylist.ui.tracks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.spotifyplaylist.R;
import com.example.spotifyplaylist.adapters.PlaylistTracksAdapter;
import com.example.spotifyplaylist.databinding.FragmentPlaylistTracksBinding;
import com.example.spotifyplaylist.MainActivity;
import com.example.spotifyplaylist.models.PlaylistItem;
import com.example.spotifyplaylist.utils.Constants;
import com.example.spotifyplaylist.utils.Resource;

public class PlaylistTracksFragment extends Fragment {

    private FragmentPlaylistTracksBinding binding;
    private PlaylistTracksAdapter playlistTracksAdapter;
    private PlaylistTracksViewModel viewModel;
    private Bundle args;
    private final String TAG = "PlaylistTracksFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_tracks, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ((MainActivity)requireActivity()).playlistTracksViewModel;

        args = getArguments();
        if(args != null) {
            PlaylistItem item = (PlaylistItem)args.getSerializable("playlist");
            viewModel.playlistId = item.getId();
        }

        setupRecyclerView();

        viewModel.getPlaylistsTracksLiveData().observe(getViewLifecycleOwner(), response -> {
            if(response instanceof Resource.Success){
                hideProgressBar();
                if (response.getData() != null) {
                    Log.i(TAG,"Got new items! " + response.getData());
                    playlistTracksAdapter.getDiffer()
                            .submitList(response.getData().getItems());
                }
            }
            else if(response instanceof Resource.Error) {
                hideProgressBar();
                if (response.getMessage() != null) {
                    Log.e(TAG,"Error occurred: " + response.getMessage());
                    Toast.makeText(requireActivity(),"Error: " + response.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            else if(response instanceof Resource.Loading) {
                showProgressBar();
            }
        });

        // get initial page of playlists
        viewModel.getPlaylistTracks(Constants.DEFAULT_COUNTRY);
    }

    private void setupRecyclerView() {
        playlistTracksAdapter = new PlaylistTracksAdapter(requireContext());
        binding.rvTracks.setAdapter(playlistTracksAdapter);
        binding.rvTracks.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    private void hideProgressBar() {
        binding.loading.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        binding.loading.setVisibility(View.VISIBLE);
    }

}