package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ViewTrackItemBinding

class TrackAdapter : RecyclerView.Adapter<TrackViewHolder>() {

    var trackList: List<Track> = emptyList()
        set(value) {
            val oldTrackList = field
            val newTrackList = value.toMutableList()

            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldTrackList.size
                }

                override fun getNewListSize(): Int {
                    return newTrackList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldTrackList[oldItemPosition].songName == newTrackList[newItemPosition].songName
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldTrackList[oldItemPosition] == newTrackList[newItemPosition]
                }
            })
            field = newTrackList
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewTrackItemBinding.inflate(layoutInflater, parent, false)
        return TrackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }
}