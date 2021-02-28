package com.example.spotifyplaylist.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Layout
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil

import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spotifyplaylist.R
import com.example.spotifyplaylist.models.PlaylistItem
import kotlinx.android.synthetic.main.playlist_item.view.*

class PlaylistAdapter(private val context: Context):
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(), Filterable {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    var playlistFilterList = ArrayList<PlaylistItem>()

    private val differCallback = object : DiffUtil.ItemCallback<PlaylistItem>() {

        override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_item,parent,false))
    }

    override fun onBindViewHolder(holder: PlaylistAdapter.ViewHolder, position: Int) {
        val playlist = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(playlist.images[0].url).into(ivPlaylistImg)
            tvPlaylistTitle.text = playlist.name
            ivPlayButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:playlist:${playlist.id}:play"))
                context.startActivity(intent)
            }
            setOnClickListener {
                onItemClickListener?.let { it(playlist) }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((PlaylistItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (PlaylistItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }


}