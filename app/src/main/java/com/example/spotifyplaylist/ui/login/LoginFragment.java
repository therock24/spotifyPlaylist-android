package com.example.spotifyplaylist.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.spotifyplaylist.R;
import com.example.spotifyplaylist.databinding.FragmentLoginBinding;
import com.example.spotifyplaylist.MainActivity;
import com.example.spotifyplaylist.utils.AppPreferences;
import com.example.spotifyplaylist.utils.Resource;

/**
 * Fragment that allows the user to login and authorize the app to use Spotify API on his/her behalf.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        viewModel = ((MainActivity)requireActivity()).loginViewModel;
        // set the login button listener
        binding.btnLogin.setOnClickListener(v -> {
            login();
        });

        // observe for the Spotify API authentication responses
        viewModel.getTokenLiveData().observe(getViewLifecycleOwner(), response -> {
            if(response instanceof Resource.Success){
                hideProgressBar();

                // update first run boolean to avoi having to login again
                AppPreferences.INSTANCE.setFirstRun(false);

                // navigate to playlist fragment
                navigateToPlaylists();
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

        return binding.getRoot();
    }

    // decides on the next action depending if there is a token already available
    private void login() {
        if(AppPreferences.INSTANCE.isFirstRun() || AppPreferences.INSTANCE.getUserToken() == null) {
            // if it is the user first run, open login window
            ((MainActivity)requireActivity()).openLoginWindow();
        } else {
            // if the token has expired
            if (AppPreferences.INSTANCE.getTokenExpiration() < (System.currentTimeMillis()/1000L)) {
                // request a new refreshed token
                viewModel.requestToken(AppPreferences.INSTANCE.getRefreshCode(),true);
            } else {
                // navigate to playlist fragment
                navigateToPlaylists();
            }
        }
    }

    private void navigateToPlaylists() {
        // navigate to playlist fragment
        Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).
                navigate(R.id.action_loginFragment_to_playlistFragment);
    }

    private void hideProgressBar() {
        binding.loading.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        binding.loading.setVisibility(View.VISIBLE);
    }

}