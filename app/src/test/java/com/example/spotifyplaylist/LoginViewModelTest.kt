package com.example.spotifyplaylist

import com.example.spotifyplaylist.models.RequestTokenResponse
import com.example.spotifyplaylist.ui.login.LoginViewModel
import com.example.spotifyplaylist.utils.Resource
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import retrofit2.Response


/**
 * Unit tests for LoginViewModel class.
 */
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel()
    }

    /**
     * Tests the token response handler for the case of a null object.
     */
    @Test
    fun test_null_response_produces_error() {

        // generate null response
        val response = null

        // get handled response
        assertThat(viewModel.handleTokenResponse(response) is Resource.Error).isTrue()
    }

}

