package com.example.playlistmaker

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ViewTrackItemBinding

class TrackViewHolder(
    private val binding: ViewTrackItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        val coverCornerRadius = 2f
        binding.apply {
            tvSongName.text = track.songName
            tvArtistName.text = track.artistName
            tvTrackTime.text = track.trackTime
            Glide.with(this.root)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            coverCornerRadius,
                            binding.root.context.resources.displayMetrics
                        ).toInt()
                    )
                )
                .into(ivCover)
        }
    }
}