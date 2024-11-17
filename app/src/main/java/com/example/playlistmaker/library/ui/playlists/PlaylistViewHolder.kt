package com.example.playlistmaker.library.ui.playlists

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Playlist.Companion.getFormattedCount
import com.example.playlistmaker.databinding.ViewPlaylistItemGridBinding
import com.example.playlistmaker.utils.convertDpToPx

class PlaylistViewHolder(
    private val binding: ViewPlaylistItemGridBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist, onItemClickListener: OnItemClickListener?) {
        binding.apply {
            root.setOnClickListener {
                onItemClickListener?.onItemClick(playlist)
            }
            tvPlaylistName.text = playlist.playlistName
            tvCount.text = playlist.getFormattedCount()

            Glide.with(this.root)
                .load(playlist.playlistCoverPath)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        binding.root.context.convertDpToPx(COVER_CORNER_RADIUS_IN_DP)
                    )
                )
                .into(ivCover)
        }
    }

    fun interface OnItemClickListener {
        fun onItemClick(playlist: Playlist)
    }

    private companion object {
        private const val COVER_CORNER_RADIUS_IN_DP = 2f
    }
}