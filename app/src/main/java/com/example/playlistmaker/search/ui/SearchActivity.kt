package com.example.playlistmaker.search.ui

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
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }

    private var searchInput = ""

    private var isClickAllowed = true

    private val trackAdapter = TrackAdapter()

    private val searchHistoryAdapter = TrackAdapter()

    private val handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.screenStateLiveData.observe(this) { screenState ->
            changeScreenState(screenState)
        }

        viewModel.isClearInputbuttonVisibileLiveData.observe(this) {
            binding.ivClear.isVisible = it
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onInputStateChanged(hasFocus, binding.editTextSearch.text)
        }

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onInputStateChanged(binding.editTextSearch.hasFocus(), s)
                searchInput = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivClear.setOnClickListener {
            binding.editTextSearch.setText("")
            hideKeyboard()
        }

        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel.repeatLastRequest()
        }

        trackAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            if (clickDebounce()) {
                viewModel.addTrackToSearchHistory(it)
                startActivity(PlayerActivity.newIntent(this, it))
            }
        }

        searchHistoryAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            if (clickDebounce()) {
                viewModel.addTrackToSearchHistory(it)
                startActivity(PlayerActivity.newIntent(this, it))
            }
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.cleanSearchHistory()
        }
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

    private fun changeScreenState(screenState: SearchActivityState) {
        when (screenState) {
            is SearchActivityState.Content -> showContent(screenState.tracks)
            SearchActivityState.Empty -> showEmpty()
            SearchActivityState.Error -> showError()
            SearchActivityState.Loading -> showLoading()
            is SearchActivityState.History -> showSearchHistory(screenState.tracks)
        }
    }

    private fun showLoading() {
        binding.apply {
            tvNothingFound.isVisible = false
            llNoConnection.isVisible = false
            searchHistoryTitle.isVisible = false
            buttonClearHistory.isVisible = false
            progressBar.isVisible = true
            rvTracks.isVisible = false
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.apply {
            tvNothingFound.isVisible = false
            llNoConnection.isVisible = false
            searchHistoryTitle.isVisible = false
            buttonClearHistory.isVisible = false
            progressBar.isVisible = false
            rvTracks.isVisible = true
            rvTracks.adapter = trackAdapter
            trackAdapter.trackList = tracks
        }
    }

    private fun showSearchHistory(trackList: List<Track>) {
        binding.apply {
            tvNothingFound.isVisible = false
            llNoConnection.isVisible = false
            searchHistoryTitle.isVisible = true
            buttonClearHistory.isVisible = true
            progressBar.isVisible = false
            rvTracks.isVisible = true
            rvTracks.adapter = searchHistoryAdapter
            searchHistoryAdapter.trackList = trackList
        }
    }

    private fun showEmpty() {
        binding.apply {
            tvNothingFound.isVisible = true
            llNoConnection.isVisible = false
            searchHistoryTitle.isVisible = false
            buttonClearHistory.isVisible = false
            progressBar.isVisible = false
            rvTracks.isVisible = false
        }
    }

    private fun showError() {
        binding.apply {
            tvNothingFound.isVisible = false
            llNoConnection.isVisible = true
            searchHistoryTitle.isVisible = false
            buttonClearHistory.isVisible = false
            progressBar.isVisible = false
            rvTracks.isVisible = false
        }
    }

    companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}