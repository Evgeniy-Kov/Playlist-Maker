package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private var searchInput = ""

    private var isClickAllowed = true

    private val trackAdapter = TrackAdapter()

    private val searchHistoryAdapter = TrackAdapter()

    private val handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.screenStateLiveData.observe(viewLifecycleOwner) { screenState ->
            changeScreenState(screenState)
        }

        viewModel.isClearInputbuttonVisibileLiveData.observe(viewLifecycleOwner) {
            binding.ivClear.isVisible = it
        }

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
                val direction = SearchFragmentDirections.actionSearchFragmentToPlayerActivity(it)
                findNavController().navigate(direction)
            }
        }

        searchHistoryAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            if (clickDebounce()) {
                viewModel.addTrackToSearchHistory(it)
                val direction = SearchFragmentDirections.actionSearchFragmentToPlayerActivity(it)
                findNavController().navigate(direction)
            }
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.cleanSearchHistory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val inputMethodManager = requireContext().getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

    private fun changeScreenState(screenState: SearchFragmentState) {
        when (screenState) {
            is SearchFragmentState.Content -> showContent(screenState.tracks)
            SearchFragmentState.Empty -> showEmpty()
            SearchFragmentState.Error -> showError()
            SearchFragmentState.Loading -> showLoading()
            is SearchFragmentState.History -> showSearchHistory(screenState.tracks)
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
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}