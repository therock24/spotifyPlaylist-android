package com.example.spotifyplaylist.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private const val NAME = "SpotifyPlaylistApp"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    /**
     * Saves time on editing a value on SharedPreferences.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    /**
     * Stores the Spotify User Token.
     */
    var userToken: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString("user_token", "")
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putString("user_token", value)
        }

    /**
     * Stores the code used to ask for a new User Token.
     * Each token has a lifetime of 1 hour. After that period, a new token must be requested.
     */
    var refreshCode: String?
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getString("user_token", "")
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putString("user_token", value)
        }
}