package com.example.playlistmaker.player.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.Track.Companion.getFormattedTime
import com.example.playlistmaker.common.domain.model.Track.Companion.getFormattedYear
import com.example.playlistmaker.common.domain.model.Track.Companion.getHighQualityCoverLink
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayStatus
import com.example.playlistmaker.utils.convertDpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var track: Track

    private val args by navArgs<PlayerActivityArgs>()

    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        track = args.track

        initializeViews()
        viewModel.prepare(track.previewUrl)
        viewModel.playStatusLiveData.observe(this) { playStatus ->
            changePlayButtonStyle(playStatus)
            setPlaybackTime(playStatus.progress)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun initializeViews() {
        Glide.with(this)
            .load(track.getHighQualityCoverLink())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(convertDpToPx(COVER_CORNER_RADIUS_IN_DP))
            )
            .into(binding.ivCover)

        binding.apply {
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = track.getFormattedTime()
            if (track.collectionName.isNullOrBlank()) {
                groupCollectionName.isVisible = false
            } else {
                tvCollectionName.text = track.collectionName
            }
            tvYear.text = track.getFormattedYear()
            tvGenre.text = track.primaryGenreName
            tvCountry.text = track.country
            buttonPlay.setOnClickListener { viewModel.playbackControl() }
        }
    }

    private fun setPlaybackTime(timeMillis: Int) {
        binding.tvPlaybackProgress.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    private fun changePlayButtonStyle(playStatus: PlayStatus) {
        val drawable = if (playStatus.isPlaying) {
            R.drawable.ic_button_pause
        } else {
            R.drawable.ic_button_play
        }
        binding.buttonPlay.setBackgroundResource(drawable)
    }

    companion object {
        private const val COVER_CORNER_RADIUS_IN_DP = 8f
    }
}