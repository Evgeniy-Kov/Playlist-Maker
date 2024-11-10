package com.example.playlistmaker.player.ui

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Playlist.Companion.getFormattedCount
import com.example.playlistmaker.databinding.ViewPlaylistItemRvBinding

class PlaylistViewHolder(
    private val binding: ViewPlaylistItemRvBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, onItemClickListener: OnItemClickListener?) {
        binding.apply {
            root.setOnClickListener {
                onItemClickListener?.onItemClick(playlist)
            }
            tvPlaylistName.text = playlist.playlistName
            tvCount.text = playlist.getFormattedCount()
            ivCover.setImageURI(Uri.parse(playlist.playlistCoverPath))
        }
    }

    fun interface OnItemClickListener {
        fun onItemClick(playlist: Playlist)
    }

    private companion object {
        private const val COVER_CORNER_RADIUS_IN_DP = 2f
    }
}