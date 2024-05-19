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

    private val editTextSearch by lazy {
        binding.editTextSearch
    }

    private val clearButton by lazy {
        binding.imageViewClearButton
    }

    private val tvNothingFound by lazy {
        binding.tvNothingFound
    }

    private val linearLayoutNoConnection by lazy {
        binding.llNoConnection
    }

    private val buttonUpdate by lazy {
        binding.buttonUpdate
    }

    private val rvTracks by lazy {
        binding.rvTracks
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

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchInput = editTextSearch.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearButton.setOnClickListener {
            editTextSearch.setText("")
            hideKeyboard()
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                queryInput = searchInput
                hideKeyboard()
                sendQuery()
                return@setOnEditorActionListener true
            }
            false
        }

        buttonUpdate.setOnClickListener {
            sendQuery()
        }

        rvTracks.adapter = trackAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_INPUT, "")
        editTextSearch.setText(searchInput)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editTextSearch.windowToken, 0)
    }

    private fun sendQuery() {
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
                            showErrorMessage(ErrorMessageType.SUCCESS)
                        } else {
                            trackAdapter.trackList = emptyList()
                            showErrorMessage(ErrorMessageType.NOTHING_FOUND)
                        }
                    }
                    else -> { showErrorMessage(ErrorMessageType.NO_CONNECTION) }
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
                linearLayoutNoConnection.isVisible = false
                tvNothingFound.isVisible = true
            }
            ErrorMessageType.NO_CONNECTION -> {
                tvNothingFound.isVisible = false
                linearLayoutNoConnection.isVisible = true
            }
            ErrorMessageType.SUCCESS -> {
                tvNothingFound.isVisible = false
                linearLayoutNoConnection.isVisible = false
            }
        }
    }

    companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"
        private const val STATUS_SUCCESS = 200
    }
}