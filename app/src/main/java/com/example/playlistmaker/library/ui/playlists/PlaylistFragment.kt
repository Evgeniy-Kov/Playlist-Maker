package com.example.playlistmaker.library.ui.playlists

import android.content.Intent
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
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Playlist.Companion.getFormattedCount
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.Track.Companion.getFormattedTime
import com.example.playlistmaker.common.ui.TrackAdapter
import com.example.playlistmaker.common.ui.TrackViewHolder
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    private val args by navArgs<PlaylistFragmentArgs>()

    private var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private val adapter = TrackAdapter()

    private var playlist: Playlist? = null

    private val tracks = mutableListOf<Track>()

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
                .setMessage(
                    requireActivity().getString(
                        R.string.delete_track_from_playlist_dialog_message
                    )
                )
                .setNegativeButton(
                    requireActivity().getString(R.string.delete_track_from_playlist_dialog_negative)
                ) { dialog, which ->
                }
                .setPositiveButton(
                    requireActivity().getString(R.string.delete_track_from_playlist_dialog_positive)
                ) { dialog, which ->
                    viewModel.deleteTrackFromPlaylist(playlist!!, track.trackId)
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

        viewModel.getPlaylist(args.playlistId)

        viewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            this.playlist = playlist
            viewModel.getTracksByIds(playlist.tracksIds)

        }


        viewModel.tracksLiveData.observe(viewLifecycleOwner) { tracks ->
            this.tracks.clear()
            this.tracks.addAll(tracks)
            setupPlaylistDescription(playlist!!)
            setupTracks(tracks)
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
            menuBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Center)
                .setTitle(requireActivity().getString(R.string.delete_playlist_dialog_title))
                .setMessage(
                    requireActivity().getString(
                        R.string.delete_playlist_dialog_message
                    )
                )
                .setNegativeButton(
                    requireActivity().getString(R.string.delete_playlist_dialog_negative)
                ) { dialog, which ->
                }
                .setPositiveButton(
                    requireActivity().getString(R.string.delete_playlist_dialog_positive)
                ) { dialog, which ->
                    viewModel.deletePlaylist(playlist!!)
                    findNavController().navigateUp()
                }
                .show()
        }

        binding.tvEditPlaylist.setOnClickListener {
            val direction =
                PlaylistFragmentDirections.actionPlaylistFragmentToNewPlaylistFragment(playlist)
            findNavController().navigate(direction)
        }

        binding.buttonShare.setOnClickListener {
            sharePlaylist()
        }

        binding.tvSharePlaylist.setOnClickListener {
            menuBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracksBottomSheetBehavior = null
        menuBottomSheetBehavior = null
        _binding = null
    }

    private fun removeOnPreDrawListener() {
        _binding ?: return
        binding.main.viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
    }

    private fun setupPlaylistDescription(playlist: Playlist) {
        binding.tvPlaylistName.text = playlist.playlistName
        binding.tvPlaylistDescription.text = playlist.playlistDescription
        binding.tvDuration.text = getFormattedDuration(tracks)
        binding.tvTracksCount.text = playlist.getFormattedCount()

        binding.tvPlaylistNameBottomSheet.text = playlist.playlistName
        val formattedCount = playlist.getFormattedCount()
        binding.tvCountBottomSheet.text = formattedCount
        if (playlist.playlistCoverPath != "null") {
            val uri = Uri.parse(playlist.playlistCoverPath)
            binding.ivCover.setImageURI(uri)
        }
        Glide.with(requireContext())
            .load(playlist.playlistCoverPath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.ivCoverBottomSheet)

    }

    private fun setupTracks(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.playlist_is_empty),
                Toast.LENGTH_SHORT
            ).show()
        }
        adapter.trackList = tracks
    }

    private fun sharePlaylist() {
        if (tracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.nothing_to_share_message),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            var listOfTracks = ""

            for (i in 0 until tracks.size) {
                val track = tracks[i]
                listOfTracks += "${i + 1}. ${track.artistName} - " +
                        "${track.trackName} (${track.getFormattedTime()})\n"
            }

            val name = "${playlist?.playlistName}\n"
            val description = if (playlist!!.playlistDescription.isBlank()) {
                ""
            } else {
                "${playlist?.playlistDescription}\n"
            }
            val count = "${playlist?.getFormattedCount()}\n"

            val message = name + description + count + listOfTracks

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun getFormattedDuration(tracks: List<Track>): String {
        var result = ""
        var duration = 0L
        for (track in tracks) {
            duration += track.trackTimeMillis.toLong()
        }
        duration = TimeUnit.MILLISECONDS.toMinutes(duration)

        val beforeLastDigit = duration % 100 / 10
        if (beforeLastDigit == 1L) {
            result = "минут"
        } else {
            when (duration % 10) {
                1L -> {
                    result = "минута"
                }

                2L, 3L, 4L -> {
                    result = "минуты"
                }

                else -> {
                    result = "минут"
                }
            }
        }
        return "${duration} $result"
    }

}
