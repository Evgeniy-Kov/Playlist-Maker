package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.databinding.ViewPlaylistItemRvBinding

class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {

    var onItemClickListener: PlaylistViewHolder.OnItemClickListener? = null
    var playlistList: List<Playlist> = emptyList()
        set(value) {
            val oldPlaylistList = field
            val newPlaylistList = value.toMutableList()

            val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return oldPlaylistList.size
                }

                override fun getNewListSize(): Int {
                    return newPlaylistList.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return oldPlaylistList[oldItemPosition].playlistId == newPlaylistList[newItemPosition].playlistId
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldPlaylistList[oldItemPosition] == newPlaylistList[newItemPosition]
                }
            })
            field = newPlaylistList
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewPlaylistItemRvBinding.inflate(layoutInflater, parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(
            playlistList[position],
            onItemClickListener
        )
    }
}