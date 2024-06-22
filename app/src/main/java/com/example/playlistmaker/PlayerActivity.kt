package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {
    private lateinit var track: Track

    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
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

        parseIntent()
        initializeViews()
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_TRACK_ITEM)) {
            throw RuntimeException(getString(R.string.track_not_found_exception))
        }
        track = Gson().fromJson(intent.getStringExtra(EXTRA_TRACK_ITEM), Track::class.java)
    }

    private fun initializeViews() {
        Glide.with(this)
            .load(track.getHighQualityCoverLink())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(convertDpToPx(resources.displayMetrics, COVER_CORNER_RADIUS_IN_DP))
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
        }
    }

    companion object {
        private const val COVER_CORNER_RADIUS_IN_DP = 8f
        private const val EXTRA_TRACK_ITEM = "extra_track_item"
        fun newIntent(context: Context, track: Track): Intent {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK_ITEM, Gson().toJson(track))
            }
            return intent
        }
    }
}