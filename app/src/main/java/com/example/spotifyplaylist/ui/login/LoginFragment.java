package com.example.spotifyplaylist.ui.login;

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

import com.example.spotifyplaylist.R;
import com.example.spotifyplaylist.databinding.FragmentLoginBinding;
import com.example.spotifyplaylist.MainActivity;
import com.example.spotifyplaylist.utils.Resource;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private String TAG = "PlaylistFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        binding.btnLogin.setOnClickListener(v -> {
            Log.i("Fragment", "Button clicked!!");
            ((MainActivity)requireActivity()).openLoginWindow();
        });

        viewModel = ((MainActivity)requireActivity()).loginViewModel;

        viewModel.getLoginLiveData().observe(getViewLifecycleOwner(),response -> {
            if(response instanceof Resource.Success){
                hideProgressBar();

                // navigate to playlist fragment
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).
                        navigate(R.id.action_loginFragment_to_playlistFragment);
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

        Log.i("Fragment", "OnCreateView()");
        return binding.getRoot();
    }

    private void hideProgressBar() {
        binding.loading.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        binding.loading.setVisibility(View.VISIBLE);
    }

   /*
    fun onRequestCodeClicked(view: View?) {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.CODE)
        AuthorizationClient.openLoginActivity(
            requireActivity(),
            AUTH_CODE_REQUEST_CODE,
            request
        )
    }

    fun onRequestTokenClicked(view: View?) {
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(
            requireActivity(),
            AUTH_TOKEN_REQUEST_CODE,
            request
        )
    }

    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
            .setShowDialog(false)
            .setScopes(arrayOf("user-read-email"))
            .setCampaign("your-campaign-token")
            .build()
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthorizationClient.getResponse(resultCode, data)
        if (response.error != null && response.error.isEmpty()) {
            setResponse(response.error)
        }
        if (requestCode == MainActivity.AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.accessToken
            updateTokenView()
        } else if (requestCode == MainActivity.AUTH_CODE_REQUEST_CODE) {
            mAccessCode = response.code
            updateCodeView()
        }
    }

    private fun setResponse(text: String) {
        runOnUiThread(Runnable {
            val responseView: TextView = findViewById(R.id.response_text_view)
            responseView.setText(text)
        })
    }

    private fun updateTokenView() {
        val tokenView: TextView = findViewById(R.id.token_text_view)
        tokenView.setText(getString(R.string.token, mAccessToken))
    }

    private fun updateCodeView() {
        val codeView: TextView = findViewById(R.id.code_text_view)
        codeView.setText(getString(R.string.code, mAccessCode))
    }

    private fun cancelCall() {
        if (mCall != null) {
            mCall.cancel()
        }
    }

    private fun getRedirectUri(): Uri {
        return Uri.Builder()
            .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
            .authority(getString(R.string.com_spotify_sdk_redirect_host))
            .build()
    }
*/

}