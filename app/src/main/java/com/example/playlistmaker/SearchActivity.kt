package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private var searchInput = ""

    private val trackAdapter = TrackAdapter()

    private var queryInput = ""

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesApiService = retrofit.create(ITunesSearchApi::class.java)

    private val trackList = mutableListOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClear.isVisible = !s.isNullOrEmpty()
                searchInput = binding.editTextSearch.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivClear.setOnClickListener {
            binding.editTextSearch.setText("")
            hideKeyboard()
            clearTrackList()
        }

        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                queryInput = searchInput
                binding.editTextSearch.setText("")
                hideKeyboard()
                sendQuery()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.buttonUpdate.setOnClickListener {
            sendQuery()
        }

        binding.rvTracks.adapter = trackAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_INPUT, "")
        binding.editTextSearch.setText(searchInput)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

    private fun sendQuery() {
        hideErrorMessage()
        clearTrackList()
        iTunesApiService.search(queryInput).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                when (response.code()) {
                    STATUS_SUCCESS -> {
                        if (!response.body()?.results.isNullOrEmpty()) {
                            trackList.clear()
                            trackList.addAll(response.body()?.results ?: emptyList())
                            trackAdapter.trackList = trackList
                        } else {
                            trackAdapter.trackList = emptyList()
                            showErrorMessage(ErrorMessageType.NOTHING_FOUND)
                        }
                    }

                    else -> {
                        showErrorMessage(ErrorMessageType.NO_CONNECTION)
                    }
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showErrorMessage(ErrorMessageType.NO_CONNECTION)
            }
        })
    }

    private fun showErrorMessage(errorType: ErrorMessageType) {
        when (errorType) {
            ErrorMessageType.NOTHING_FOUND -> {
                binding.llNoConnection.isVisible = false
                binding.tvNothingFound.isVisible = true
            }

            ErrorMessageType.NO_CONNECTION -> {
                binding.tvNothingFound.isVisible = false
                binding.llNoConnection.isVisible = true
            }
        }
    }

    private fun hideErrorMessage() {
        binding.tvNothingFound.isVisible = false
        binding.llNoConnection.isVisible = false
    }

    private fun clearTrackList() {
        trackList.clear()
        trackAdapter.trackList = trackList
    }

    companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"
        private const val STATUS_SUCCESS = 200
    }
}