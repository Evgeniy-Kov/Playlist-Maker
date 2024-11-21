package com.example.playlistmaker.library.ui.playlists

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Playlist.Companion.getFormattedCount
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks.Companion.getFormattedDuration
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.ui.TrackAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    private val args by navArgs<PlaylistFragmentArgs>()

    private val tracksBottomSheetBehavior by lazy {
        BottomSheetBehavior.from(binding.tracksBottomSheet)
    }

    private val adapter = TrackAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                val screenHeight = binding.main.height
                val playlistDescriptionHeight = binding.constraint.height

                tracksBottomSheetBehavior.setPeekHeight(screenHeight - playlistDescriptionHeight)
                binding.constraint.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        binding.constraint.viewTreeObserver.addOnGlobalLayoutListener(listener)

        binding.rvPlaylist.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.getPlaylistWithTracks(args.playlistId)

        viewModel.playlistWithTracksLiveData.observe(viewLifecycleOwner) { playlistWithTracks ->
            setupPlaylistDescription(playlistWithTracks)
            setupTracks(playlistWithTracks.tracks)

        }
    }

    private fun setupPlaylistDescription(playlistWithTracks: PlaylistWithTracks) {
        binding.tvPlaylistName.text = playlistWithTracks.playlist.playlistName
        binding.tvPlaylistDescription.text = playlistWithTracks.playlist.playlistDescription
        binding.tvDuration.text = playlistWithTracks.getFormattedDuration()
        binding.tvTracksCount.text = playlistWithTracks.playlist.getFormattedCount()
        if (playlistWithTracks.playlist.playlistCoverPath != "null") {
            binding.ivCover.setImageURI(Uri.parse(playlistWithTracks.playlist.playlistCoverPath))
        }

    }

    private fun setupTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.playlist_is_empty),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            adapter.trackList = tracks
        }
    }

}
