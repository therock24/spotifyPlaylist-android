package com.example.spotifyplaylist.db

import androidx.room.TypeConverter
import com.example.spotifyplaylist.models.PlaylistImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Converters to store Playlist Image URLs on Room Database.
 */
class Converters {
    @TypeConverter
    fun fromPlaylistImageArray(playlistImages: List<PlaylistImage>): String {
        return Gson().toJson(playlistImages)
    }

    @TypeConverter
    fun toPlaylistImageArray(urls: String): List<PlaylistImage> {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(urls, listType)
    }
}