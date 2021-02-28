package com.example.spotifyplaylist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spotifyplaylist.models.PlaylistItem

@Database(
    entities = [PlaylistItem::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class PlaylistDatabase: RoomDatabase() {

    abstract fun getPlaylistDao(): PlaylistDao

    companion object {
        @Volatile
        private var instance: PlaylistDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
            PlaylistDatabase::class.java,
            "playlist_db.db").build()
    }
}