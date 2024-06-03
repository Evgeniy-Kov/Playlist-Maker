package com.example.playlistmaker

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ViewTrackItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(
    private val binding: ViewTrackItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track, onItemClickListener: OnItemClickListener?) {
        val coverCornerRadius = 2f
        binding.apply {
            root.setOnClickListener {
                onItemClickListener?.onItemClick(track)
            }
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(track.trackTimeMillis)

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

    fun interface OnItemClickListener {
        fun onItemClick(track: Track)
    }
}