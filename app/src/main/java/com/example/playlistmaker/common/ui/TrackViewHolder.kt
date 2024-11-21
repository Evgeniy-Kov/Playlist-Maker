package com.example.playlistmaker.common.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.Track.Companion.getFormattedTime
import com.example.playlistmaker.databinding.ViewTrackItemBinding
import com.example.playlistmaker.utils.convertDpToPx

class TrackViewHolder(
    private val binding: ViewTrackItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        track: Track,
        onItemClickListener: OnItemClickListener?,
        onLongClickListener: OnLongClickListener?
    ) {
        binding.apply {
            root.setOnClickListener {
                onItemClickListener?.onItemClick(track)
            }
            root.setOnLongClickListener {
                onLongClickListener?.onLongClick(track)
                true
            }
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = track.getFormattedTime()

            Glide.with(this.root)
                .load(track.artworkUrl100)
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
        fun onItemClick(track: Track)
    }

    fun interface OnLongClickListener {
        fun onLongClick(track: Track)
    }

    private companion object {
        private const val COVER_CORNER_RADIUS_IN_DP = 2f
    }
}