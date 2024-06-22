package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        parseIntent()
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_TRACK_ITEM)) {
            throw RuntimeException(getString(R.string.track_not_found_exception))
        }
        track = Gson().fromJson(intent.getStringExtra(EXTRA_TRACK_ITEM), Track::class.java)
    }

    companion object {
        private const val EXTRA_TRACK_ITEM = "extra_track_item"
        fun newIntent(context: Context, track: Track): Intent {
            val intent = Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_TRACK_ITEM, Gson().toJson(track))
            }
            return intent
        }
    }
}