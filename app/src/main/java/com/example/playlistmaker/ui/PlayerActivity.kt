package com.example.playlistmaker.ui

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.model.Track.Companion.getFormattedTime
import com.example.playlistmaker.domain.model.Track.Companion.getFormattedYear
import com.example.playlistmaker.domain.model.Track.Companion.getHighQualityCoverLink
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var track: Track

    private var playerState = PLAYER_STATE_DEFAULT

    private val mediaPlayer = MediaPlayer()

    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val handler = Handler(Looper.getMainLooper())

    private val updatePlaybackTimeRunnable by lazy {
        createUpdatePlaybackTimeTask()
    }

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
            finish()
        }

        if (!parseIntent()) {
            finish()
            return
        }
        initializeViews()
        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        playerPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun parseIntent(): Boolean {
        if (!intent.hasExtra(EXTRA_TRACK_ITEM) && intent.getParcelableExtra<Track>(EXTRA_TRACK_ITEM) == null) {
            Toast.makeText(
                this,
                getString(R.string.track_not_found_message),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        track = intent.getParcelableExtra(EXTRA_TRACK_ITEM)!!
        return true
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
            buttonPlay.setOnClickListener { playbackControl() }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PLAYER_STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            setPlaybackTime(PLAYER_DEFAULT_PLAYBACK_TIME)
            binding.buttonPlay.setBackgroundResource(R.drawable.ic_button_play)
            playerState = PLAYER_STATE_PREPARED
        }
    }

    private fun playerPlay() {
        mediaPlayer.start()
        binding.buttonPlay.setBackgroundResource(R.drawable.ic_button_pause)
        playerState = PLAYER_STATE_PLAYING
        handler.post(updatePlaybackTimeRunnable)
    }

    private fun playerPause() {
        mediaPlayer.pause()
        binding.buttonPlay.setBackgroundResource(R.drawable.ic_button_play)
        playerState = PLAYER_STATE_PAUSED
        handler.removeCallbacks(updatePlaybackTimeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            PLAYER_STATE_PLAYING -> playerPause()
            PLAYER_STATE_PREPARED, PLAYER_STATE_PAUSED -> playerPlay()
        }
    }

    private fun setPlaybackTime(timeMillis: Int) {
        binding.tvPlaybackProgress.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    private fun createUpdatePlaybackTimeTask(): Runnable {
        return object : Runnable {
            override fun run() {
//                val currentPlaybackTime = mediaPlayer.currentPosition
                if (playerState == PLAYER_STATE_PLAYING) {
                    setPlaybackTime(mediaPlayer.currentPosition)
                    handler.postDelayed(this, PLAYER_UPDATE_PLAYBACK_TIME_DELAY_MILLIS)
                }
            }
        }
    }

    companion object {
        private const val COVER_CORNER_RADIUS_IN_DP = 8f
        private const val EXTRA_TRACK_ITEM = "extra_track_item"
        private const val PLAYER_STATE_DEFAULT = 0
        private const val PLAYER_STATE_PREPARED = 1
        private const val PLAYER_STATE_PLAYING = 2
        private const val PLAYER_STATE_PAUSED = 3
        private const val PLAYER_DEFAULT_PLAYBACK_TIME = 0
        private const val PLAYER_UPDATE_PLAYBACK_TIME_DELAY_MILLIS = 100L
        fun newIntent(context: Context, track: Track): Intent {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK_ITEM, track)
            }
            return intent
        }
    }
}