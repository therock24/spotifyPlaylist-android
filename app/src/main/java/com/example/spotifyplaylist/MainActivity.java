package com.example.spotifyplaylist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;


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


import com.example.spotifyplaylist.R;


public class MainActivity extends AppCompatActivity {

    public LoginViewModel loginViewModel;
    public PlaylistViewModel playlistViewModel;
    public PlaylistTracksViewModel playlistTracksViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppPreferences.INSTANCE.init(this.getApplicationContext());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.main_navigation);
        navGraph.setStartDestination(R.id.loginFragment);
        navController.setGraph(navGraph);

        loginViewModel = new LoginViewModel();

        PlaylistRepository playlistRepository = new PlaylistRepository(PlaylistDatabase.Companion.invoke(this));
        PlaylistViewModelProviderFactory vmFactory = new PlaylistViewModelProviderFactory(playlistRepository);
        playlistViewModel = new ViewModelProvider(this,vmFactory).get(PlaylistViewModel.class);

        playlistTracksViewModel = new PlaylistTracksViewModel(playlistRepository);

        Log.i("Activity","OnCreate()");

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

                    // store access token and refresh token
                    AppPreferences.INSTANCE.setRefreshCode(response.getCode());

                    loginViewModel.requestToken(response.getCode());

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
/*
    public void onRequestCodeClicked(View view) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request);
    }

    public void onRequestTokenClicked(View view) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (response.getError() != null && response.getError().isEmpty()) {
            setResponse(response.getError());
        }
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            updateTokenView();
        } else if (requestCode == AUTH_CODE_REQUEST_CODE) {
            mAccessCode = response.getCode();
            updateCodeView();
        }
    }

    private void setResponse(final String text) {
        runOnUiThread(() -> {
            final TextView responseView = findViewById(R.id.response_text_view);
            responseView.setText(text);
        });
    }

    private void updateTokenView() {
        final TextView tokenView = findViewById(R.id.token_text_view);
        tokenView.setText(getString(R.string.token, mAccessToken));
    }

    private void updateCodeView() {
        final TextView codeView = findViewById(R.id.code_text_view);
        codeView.setText(getString(R.string.code, mAccessCode));
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
    }


    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("MainActivity", "Login failed");
    }*/
}
