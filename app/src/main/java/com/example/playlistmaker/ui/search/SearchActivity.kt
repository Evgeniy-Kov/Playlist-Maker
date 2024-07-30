package com.example.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.playlistmaker.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.Consumer
import com.example.playlistmaker.domain.api.ConsumerData
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.PlayerActivity

class SearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private var searchInput = ""

    private var isClickAllowed = true

    private val trackAdapter = TrackAdapter()

    private val searchHistoryAdapter = TrackAdapter()

    private var queryInput = ""

    private val trackList = mutableListOf<Track>()

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable by lazy {
        Runnable {
            sendQuery()
        }
    }

    private var consumerRunnable: Runnable? = null

    private val tracksInteractor = Creator.provideTracksInteractor()

    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchHistoryAdapter.trackList = searchHistoryInteractor.getSearchHistory()

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.editTextSearch.setOnFocusChangeListener { editText, hasFocus ->
            val screenMode = if (
                hasFocus
                && binding.editTextSearch.text.isEmpty()
                && searchHistoryInteractor.getSearchHistory().isNotEmpty()
            ) {
                SearchScreenMode.SEARCH_HISTORY_SCREEN
            } else {
                SearchScreenMode.NORMAL_SCREEN
            }
            changeSearchScreenMode(screenMode)
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClear.isVisible = !s.isNullOrEmpty()
                searchInput = binding.editTextSearch.text.toString()
                var screenMode: SearchScreenMode
                if (
                    binding.editTextSearch.hasFocus()
                    && binding.editTextSearch.text.isEmpty()
                    && searchHistoryInteractor.getSearchHistory().isNotEmpty()
                ) {
                    clearTrackList()
                    handler.removeCallbacks(searchRunnable)
                    screenMode = SearchScreenMode.SEARCH_HISTORY_SCREEN
                } else {
                    searchDebounce()
                    screenMode = SearchScreenMode.NORMAL_SCREEN
                }
                changeSearchScreenMode(screenMode)
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
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.buttonUpdate.setOnClickListener {
            sendQuery()
        }

        tracksInteractor.searchTracks("sss", {
            consumerData ->

        })

        trackAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            if (clickDebounce()) {
                addTrackToSearchHistory(it)
                startActivity(PlayerActivity.newIntent(this, it))
            }
        }

        searchHistoryAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            if (clickDebounce()) {
                addTrackToSearchHistory(it)
                startActivity(PlayerActivity.newIntent(this, it))
            }
        }

        binding.buttonClearHistory.setOnClickListener {
            clearSearchHistory()
        }

        changeSearchScreenMode(SearchScreenMode.NORMAL_SCREEN)
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

    override fun onDestroy() {
        super.onDestroy()
        consumerRunnable?.let { runnable ->
            handler.removeCallbacks(runnable)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

    private fun sendQuery() {
        queryInput = searchInput
        if (queryInput.isNotBlank()) {
            changeSearchScreenMode(SearchScreenMode.WAITING)
            tracksInteractor.searchTracks(
                expression = queryInput,
                consumer = { data ->
                    val runnable = Runnable {
                        when (data) {
                            is ConsumerData.Data -> {
                                if (!data.value.isNullOrEmpty()) {
                                    trackList.clear()
                                    trackList.addAll(data.value)
                                    trackAdapter.trackList = trackList
                                    changeSearchScreenMode(SearchScreenMode.NORMAL_SCREEN)
                                } else {
                                    trackAdapter.trackList = emptyList()
                                    changeSearchScreenMode(SearchScreenMode.NOTHING_FOUND)
                                }
                            }

                            is ConsumerData.Error -> {
                                changeSearchScreenMode(SearchScreenMode.NO_CONNECTION)
                            }
                        }
                    }
                    consumerRunnable = runnable
                    handler.post(runnable)
                }
            )
        }
    }

    private fun changeSearchScreenMode(screenMode: SearchScreenMode) {
        when (screenMode) {
            SearchScreenMode.NORMAL_SCREEN -> {
                binding.apply {
                    tvNothingFound.isVisible = false
                    llNoConnection.isVisible = false
                    searchHistoryTitle.isVisible = false
                    buttonClearHistory.isVisible = false
                    progressBar.isVisible = false
                    rvTracks.isVisible = true
                    rvTracks.adapter = trackAdapter
                }
            }

            SearchScreenMode.SEARCH_HISTORY_SCREEN -> {
                binding.apply {
                    tvNothingFound.isVisible = false
                    llNoConnection.isVisible = false
                    searchHistoryTitle.isVisible = true
                    buttonClearHistory.isVisible = true
                    progressBar.isVisible = false
                    rvTracks.isVisible = true
                    rvTracks.adapter = searchHistoryAdapter
                }
            }

            SearchScreenMode.NOTHING_FOUND -> {
                binding.apply {
                    tvNothingFound.isVisible = true
                    llNoConnection.isVisible = false
                    searchHistoryTitle.isVisible = false
                    buttonClearHistory.isVisible = false
                    progressBar.isVisible = false
                    rvTracks.isVisible = false
                }
            }

            SearchScreenMode.NO_CONNECTION -> {
                binding.apply {
                    tvNothingFound.isVisible = false
                    llNoConnection.isVisible = true
                    searchHistoryTitle.isVisible = false
                    buttonClearHistory.isVisible = false
                    progressBar.isVisible = false
                    rvTracks.isVisible = false
                }
            }

            SearchScreenMode.WAITING -> {
                binding.apply {
                    tvNothingFound.isVisible = false
                    llNoConnection.isVisible = false
                    searchHistoryTitle.isVisible = false
                    buttonClearHistory.isVisible = false
                    progressBar.isVisible = true
                    rvTracks.isVisible = false
                }
            }
        }
    }

    private fun clearTrackList() {
        trackList.clear()
        trackAdapter.trackList = trackList
    }

    private fun addTrackToSearchHistory(track: Track) {
        searchHistoryInteractor.addTrackToSearchHistory(track)
        searchHistoryAdapter.trackList = searchHistoryInteractor.getSearchHistory()
    }

    private fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
        searchHistoryAdapter.trackList = emptyList()
        changeSearchScreenMode(SearchScreenMode.NORMAL_SCREEN)
    }

    companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}