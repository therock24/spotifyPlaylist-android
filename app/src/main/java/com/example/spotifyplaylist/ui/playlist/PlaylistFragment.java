package com.example.spotifyplaylist.ui.playlist;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.spotifyplaylist.R;
import com.example.spotifyplaylist.adapters.PlaylistAdapter;
import com.example.spotifyplaylist.databinding.FragmentPlaylistBinding;
import com.example.spotifyplaylist.MainActivity;
import com.example.spotifyplaylist.utils.Constants;
import com.example.spotifyplaylist.utils.Resource;

public class PlaylistFragment extends Fragment  {

    private FragmentPlaylistBinding binding;
    private PlaylistAdapter playlistAdapter;
    private PlaylistViewModel viewModel;
    private String TAG = "PlaylistFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ((MainActivity)requireActivity()).playlistViewModel;
        setupRecyclerView();

        playlistAdapter.setOnItemClickListener(playlist -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlist",playlist);
            Log.i(TAG,"Clicked PLaylist!");
            Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)
                    .navigate(R.id.action_playlistFragment_to_playlistTracksFragment,bundle);
            return null;
        });

        viewModel.getPlaylistsLiveData().observe(getViewLifecycleOwner(), response -> {
            if(response instanceof Resource.Success){
                hideProgressBar();
                if (response.getData() != null) {
                    Log.i(TAG,"Got new items! " + response.getData().getPlaylists().getItems());
                    playlistAdapter.getDiffer()
                            .submitList(response.getData().getPlaylists().getItems());
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
        viewModel.getFeaturedPlaylists(Constants.DEFAULT_COUNTRY);
    }

    private void setupRecyclerView() {
        playlistAdapter = new PlaylistAdapter(requireContext());
        binding.rvPlaylists.setAdapter(playlistAdapter);
        binding.rvPlaylists.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    private void hideProgressBar() {
        binding.loading.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        binding.loading.setVisibility(View.VISIBLE);
    }

}