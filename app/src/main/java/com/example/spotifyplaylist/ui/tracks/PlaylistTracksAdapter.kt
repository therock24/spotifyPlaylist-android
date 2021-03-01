package com.example.spotifyplaylist.ui.tracks

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

import com.example.spotifyplaylist.models.PlaylistTrackItem
import kotlinx.android.synthetic.main.playlist_track_item.view.*
import java.util.*

/**
 * Adapter that manages Track items of the RecyclerView in [PlaylistTracksFragment].
 */
class PlaylistTracksAdapter: RecyclerView.Adapter<PlaylistTracksAdapter.ViewHolder>(), Filterable {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    // callback for checking unique items (to avoid duplicates)
    private val differCallback = object : DiffUtil.ItemCallback<PlaylistTrackItem>() {

        override fun areItemsTheSame(oldItem: PlaylistTrackItem, newItem: PlaylistTrackItem): Boolean {
            return oldItem.track.id == newItem.track.id
        }

        override fun areContentsTheSame(oldItem: PlaylistTrackItem, newItem: PlaylistTrackItem): Boolean {
            return oldItem == newItem
        }
    }

    // differ for adding only new unique items to the list
    val differ = AsyncListDiffer(this, differCallback)

    // filtered list of tracks
    var trackFilterList: MutableList<PlaylistTrackItem> = mutableListOf()

    // filter to apply when using the search bar
    private val trackFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val charSearch = constraint.toString()
            trackFilterList = if (charSearch.isEmpty()) {
                differ.currentList
            } else {
                val resultList = mutableListOf<PlaylistTrackItem>()
                for (element in differ.currentList) {
                    if (element.track.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                        resultList.add(element)
                    }
                }
                resultList
            }

            // return filtered results
            val filterResults = FilterResults()
            filterResults.values = trackFilterList
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            trackFilterList = results?.values as MutableList<PlaylistTrackItem>
            Log.i("Adapter Tracks", "Filter list -> $trackFilterList")
            Log.i("Adapter Tracks", "All list -> ${differ.currentList}")

            notifyDataSetChanged()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_track_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = trackFilterList[position]
        holder.itemView.apply {
            // load data into the track card
            Glide.with(this).load(item.track.album.images[0].url).into(ivTrackImg)
            tvTrackName.text = item.track.name
            tvTrackArtist.text = item.track.artists[0].name
            tvTrackAlbum.text = item.track.album.name

            // set listener for the play track button
            ivPlayButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:track:${item.track.id}:play"))
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = trackFilterList.size

    /**
     * Submit new items for the tracks list.
     */
    fun submitItems(responseItems: MutableList<PlaylistTrackItem>) {
        differ.submitList(responseItems.toList());
        trackFilterList = responseItems
    }

    override fun getFilter(): Filter {
        return trackFilter
    }
}