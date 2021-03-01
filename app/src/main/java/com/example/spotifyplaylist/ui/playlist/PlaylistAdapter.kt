package com.example.spotifyplaylist.ui.playlist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import com.example.spotifyplaylist.ui.tracks.PlaylistTracksFragment
import kotlinx.android.synthetic.main.playlist_item.view.*
import java.util.*

/**
 * Adapter that manages Track items of the RecyclerView in [PlaylistTracksFragment].
 */
class PlaylistAdapter: RecyclerView.Adapter<PlaylistAdapter.ViewHolder>(), Filterable {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    // callback for checking unique items (to avoid duplicates)
    private val differCallback = object : DiffUtil.ItemCallback<PlaylistItem>() {

        override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem == newItem
        }
    }

    // differ for adding only new unique items to the list
    val differ = AsyncListDiffer(this,differCallback)

    // filtered list of tracks
    var playlistFilterList: MutableList<PlaylistItem> = differ.currentList

    // filter to apply when using the search bar
    private val playlistFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            playlistFilterList = if (charSearch.isEmpty()) {
                differ.currentList
            } else {
                val resultList = mutableListOf<PlaylistItem>()
                for (element in differ.currentList) {
                    if (element.name?.toLowerCase(Locale.ROOT)?.contains(charSearch.toLowerCase(Locale.ROOT)) == true) {
                        resultList.add(element)
                    }
                }
                resultList
            }

            // return filtered results
            val filterResults = FilterResults()
            filterResults.values = playlistFilterList
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            playlistFilterList = results?.values as MutableList<PlaylistItem>
            notifyDataSetChanged()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlistFilterList[position]
        holder.itemView.apply {
            // load data into the playlist card
            Glide.with(this).load(playlist.images?.get(0)?.url).into(ivPlaylistImg)
            tvPlaylistTitle.text = playlist.name

            // set listener for the play track button
            ivPlayButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:playlist:${playlist.id}:play"))
                context.startActivity(intent)
            }

            // set click listener for the playlist item
            setOnClickListener {
                onItemClickListener?.let { it(playlist) }
            }
        }
    }

    override fun getItemCount(): Int = playlistFilterList.size

    /**
     * Submit new items for the tracks list.
     */
    fun submitItems(responseItems: MutableList<PlaylistItem>) {
        Log.i("Adapter Playlist","response items contains " + responseItems.toList())
        differ.submitList(responseItems.toList());
        Log.i("Adapter Playlist","differ current list contains " + responseItems.toList())
        playlistFilterList = responseItems
    }

    private var onItemClickListener: ((PlaylistItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (PlaylistItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun getFilter(): Filter {
        return playlistFilter
    }


}