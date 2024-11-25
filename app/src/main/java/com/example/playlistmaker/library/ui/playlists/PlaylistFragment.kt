package com.example.playlistmaker.library.ui.playlists

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Playlist.Companion.getFormattedCount
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks
import com.example.playlistmaker.common.domain.model.PlaylistWithTracks.Companion.getFormattedDuration
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.ui.TrackAdapter
import com.example.playlistmaker.common.ui.TrackViewHolder
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    private val args by navArgs<PlaylistFragmentArgs>()

    private var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private val adapter = TrackAdapter()

    private var playlist: Playlist? = null

    private val onPreDrawListener = ViewTreeObserver.OnPreDrawListener {
        val screenHeight = binding.main.height
        val playlistDescriptionHeight = binding.constraint.height

        tracksBottomSheetBehavior?.peekHeight = screenHeight - playlistDescriptionHeight
        GlobalScope.launch {
            delay(1000)
            removeOnPreDrawListener()
        }

        true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)

        binding.main.viewTreeObserver.addOnPreDrawListener(onPreDrawListener)

        binding.rvPlaylist.adapter = adapter

        adapter.onLongClickListener = TrackViewHolder.OnLongClickListener { track ->
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
                .setTitle(requireActivity().getString(R.string.delete_track_from_playlist_dialog_title))
                .setMessage(requireActivity().getString(
                    R.string.delete_track_from_playlist_dialog_message)
                )
                .setNegativeButton(
                    requireActivity().getString(R.string.delete_track_from_playlist_dialog_negative)
                ) { dialog, which ->
                }
                .setPositiveButton(
                    requireActivity().getString(R.string.delete_track_from_playlist_dialog_positive)
                ) { dialog, which ->
                    viewModel.deleteTrackFromPlaylist(args.playlistId, track.trackId)
                }
                .show()
        }

        adapter.onItemClickListener = TrackViewHolder.OnItemClickListener {
            val direction = PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(it)
            findNavController().navigate(direction)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.getPlaylistWithTracks(args.playlistId)

        viewModel.playlistWithTracksLiveData.observe(viewLifecycleOwner) { playlistWithTracks ->
            playlist = playlistWithTracks.playlist
            setupPlaylistDescription(playlistWithTracks)
            setupTracks(playlistWithTracks.tracks)

        }

        menuBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        menuBottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.buttonMenu.setOnClickListener {
            menuBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        binding.tvDeletePlaylist.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
                .setTitle(requireActivity().getString(R.string.delete_playlist_dialog_title))
                .setMessage(requireActivity().getString(
                    R.string.delete_playlist_dialog_message)
                )
                .setNegativeButton(
                    requireActivity().getString(R.string.delete_playlist_dialog_negative)
                ) { dialog, which ->
                }
                .setPositiveButton(
                    requireActivity().getString(R.string.delete_playlist_dialog_positive)
                ) { dialog, which ->
                    viewModel.deletePlaylist(args.playlistId)
                    findNavController().navigateUp()
                }
                .show()
        }

        binding.tvEditPlaylist.setOnClickListener {
            val direction = PlaylistFragmentDirections.actionPlaylistFragmentToNewPlaylistFragment(playlist)
            findNavController().navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracksBottomSheetBehavior = null
        menuBottomSheetBehavior = null
        _binding = null
    }

    private fun removeOnPreDrawListener() {
        binding.main.viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
    }

    private fun setupPlaylistDescription(playlistWithTracks: PlaylistWithTracks) {
        binding.tvPlaylistName.text = playlistWithTracks.playlist.playlistName
        binding.tvPlaylistDescription.text = playlistWithTracks.playlist.playlistDescription
        binding.tvDuration.text = playlistWithTracks.getFormattedDuration()
        binding.tvTracksCount.text = playlistWithTracks.playlist.getFormattedCount()

        binding.tvPlaylistNameBottomSheet.text = playlistWithTracks.playlist.playlistName
        binding.tvCountBottomSheet.text = playlistWithTracks.playlist.getFormattedCount()

        if (playlistWithTracks.playlist.playlistCoverPath != "null") {
            val uri = Uri.parse(playlistWithTracks.playlist.playlistCoverPath)
            binding.ivCover.setImageURI(uri)
            binding.ivCoverBottomSheet.setImageURI(uri)
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
