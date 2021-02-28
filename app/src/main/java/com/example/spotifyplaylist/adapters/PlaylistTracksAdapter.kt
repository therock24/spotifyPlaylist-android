package com.example.spotifyplaylist.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Layout
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyplaylist.R
import com.example.spotifyplaylist.models.PlaylistItem
import com.example.spotifyplaylist.models.PlaylistTrack
import com.example.spotifyplaylist.models.PlaylistTrackItem
import kotlinx.android.synthetic.main.playlist_item.view.*
import kotlinx.android.synthetic.main.playlist_track_item.view.ivPlayButton
import kotlinx.android.synthetic.main.playlist_track_item.view.*

class PlaylistTracksAdapter(private val context: Context):
    RecyclerView.Adapter<PlaylistTracksAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<PlaylistTrackItem>() {

        override fun areItemsTheSame(oldItem: PlaylistTrackItem, newItem: PlaylistTrackItem): Boolean {
            return oldItem.track.id == newItem.track.id
        }

        override fun areContentsTheSame(oldItem: PlaylistTrackItem, newItem: PlaylistTrackItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTracksAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_track_item,parent,false))
    }

    override fun onBindViewHolder(holder: PlaylistTracksAdapter.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(item.track.album.images[0].url).into(ivTrackImg)
            tvTrackName.text = item.track.name
            tvTrackArtist.text = item.track.artists[0].name
            tvTrackAlbum.text = item.track.album.name
            ivPlayButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:track:${item.track.id}:play"))
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}