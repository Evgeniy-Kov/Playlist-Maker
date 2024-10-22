package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.ui.TrackAdapter
import com.example.playlistmaker.common.ui.TrackViewHolder
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : Fragment() {

    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!

    private val trackAdapter = TrackAdapter()

    private val viewModel by viewModel<FavouriteTracksViewModel>()

    private var isClickAllowed = true

    override fun onStart() {
        super.onStart()
        viewModel.getFavouriteTracks()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.screenStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is FavouriteTracksFragmentState.Content -> {
                    showContent(it.tracks)
                }

                FavouriteTracksFragmentState.Empty -> {
                    showPlaceholder()
                }
            }
        }

        binding.rvTracks.adapter = trackAdapter

        trackAdapter.onItemClickListener = TrackViewHolder.OnItemClickListener { track ->
            if (clickDebounce()) {
                onTrackClick(track)
            }
        }

    }

    private fun showContent(tracks: List<Track>) {
        binding.apply {
            tvNothingFound.isVisible = false
            rvTracks.isVisible = true
            rvTracks.adapter = trackAdapter
            trackAdapter.trackList = tracks
        }
    }

    private fun showPlaceholder() {
        binding.apply {
            tvNothingFound.isVisible = true
            rvTracks.isVisible = false
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        val direction =
            MediaLibraryFragmentDirections.actionFavouriteTracksFragmentToPlayerActivity(track)
        findNavController().navigate(direction)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

        fun newInstance() =
            FavouriteTracksFragment()
    }
}