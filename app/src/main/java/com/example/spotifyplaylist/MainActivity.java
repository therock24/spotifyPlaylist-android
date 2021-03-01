package com.example.spotifyplaylist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import com.example.spotifyplaylist.db.PlaylistDatabase;
import com.example.spotifyplaylist.repository.PlaylistRepository;
import com.example.spotifyplaylist.ui.login.LoginViewModel;
import com.example.spotifyplaylist.ui.playlist.PlaylistViewModel;
import com.example.spotifyplaylist.ui.playlist.PlaylistViewModelProviderFactory;
import com.example.spotifyplaylist.ui.tracks.PlaylistTracksViewModel;
import com.example.spotifyplaylist.utils.AppPreferences;
import com.example.spotifyplaylist.utils.Constants;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import com.spotify.sdk.android.auth.AuthorizationRequest;


public class MainActivity extends AppCompatActivity {

    public LoginViewModel loginViewModel;
    public PlaylistViewModel playlistViewModel;
    public PlaylistTracksViewModel playlistTracksViewModel;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SpotifyPlaylist);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppPreferences.INSTANCE.init(this.getApplicationContext());

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.main_navigation);

        navGraph.setStartDestination(R.id.loginFragment);
        navController.setGraph(navGraph);

        loginViewModel = new LoginViewModel();

        PlaylistRepository playlistRepository = new PlaylistRepository(PlaylistDatabase.Companion.invoke(this));
        PlaylistViewModelProviderFactory vmFactory = new PlaylistViewModelProviderFactory(playlistRepository);
        playlistViewModel = new ViewModelProvider(this,vmFactory).get(PlaylistViewModel.class);

        playlistTracksViewModel = new PlaylistTracksViewModel(playlistRepository);

        // Disable night mode (to avoid different theme)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("Login","OnNewIntent()");

        Uri uri = intent.getData();
        if (uri != null) {
            AuthorizationResponse response = AuthorizationResponse.fromUri(uri);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case CODE:
                    Log.i("Login","Login was successful! Token -> " + response.getAccessToken() + " Code -> " + response.getCode());
                    loginViewModel.requestToken(response.getCode(),false);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.i("Login","Login Failed!!");
                    showAToast("Login Failed! Reason: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.i("Login","Unknown Error!");
                    showAToast("Unknown Error! Please Try Again!");
            }

        }
    }

    public void openLoginWindow() {
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(Constants.SPOTIFY_CLIENT_ID,
                        AuthorizationResponse.Type.CODE, Constants.SPOTIFY_REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginInBrowser(this, request);

    }

    public void showAToast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }
}
