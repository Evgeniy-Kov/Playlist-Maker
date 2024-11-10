package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.domain.model.Track.Companion.getFormattedTime
import com.example.playlistmaker.common.domain.model.Track.Companion.getHighQualityCoverLink
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayStatus
import com.example.playlistmaker.utils.convertDpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private lateinit var track: Track

//    private val playlistsContainTrack = mutableListOf<Playlist>()

    private val playlistAdapter = PlaylistAdapter()

    private val args by navArgs<PlayerFragmentArgs>()

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(binding.playlistsBottomSheet)
    }

    private val viewModel by viewModel<PlayerViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        track = args.track

        initializeViews()
        viewModel.prepare(track.previewUrl)
        viewModel.playStatusLiveData.observe(viewLifecycleOwner) { playStatus ->
            changePlayButtonStyle(playStatus)
            setPlaybackTime(playStatus.progress)
        }

        binding.buttonLike.setOnClickListener {
            viewModel.onFavouriteButtonPressed(track)
        }

        viewModel.isFavouriteTrackLiveData.observe(viewLifecycleOwner) { isFavourite ->
            setButtonLikeResource(isFavourite)
            track.isFavourite = isFavourite
        }

        viewModel.playlistsLiveData.observe(viewLifecycleOwner) { playlists ->
            binding.rvPlaylist.adapter = playlistAdapter
            playlistAdapter.playlistList = playlists
        }

        viewModel.resultOfAddingTrackToPlaylistLiveData.observe(viewLifecycleOwner) { resultMessage ->
            showResultOfAddingTrack(resultMessage)
        }

        viewModel.setIsFavouriteFlag(track.isFavourite)

        binding.buttonNewPlaylist.setOnClickListener {
            val direction = PlayerFragmentDirections.actionPlayerFragmentToNewPlaylistFragment()
            findNavController().navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun initializeViews() {
        Glide.with(this)
            .load(track.getHighQualityCoverLink())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(
                RoundedCorners(requireContext().convertDpToPx(COVER_CORNER_RADIUS_IN_DP))
            )
            .into(binding.ivCover)

        binding.apply {

            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = track.getFormattedTime()
            if (track.collectionName.isNullOrBlank()) {
                groupCollectionName.isVisible = false
            } else {
                tvCollectionName.text = track.collectionName
            }
            tvYear.text = track.releaseDate
            tvGenre.text = track.primaryGenreName
            tvCountry.text = track.country
            buttonPlay.setOnClickListener { viewModel.playbackControl() }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            buttonAdd.setOnClickListener {
//                viewModel.getPlaylists()
//                viewModel.getPlaylistsContainTrack(track.trackId)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }

            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {

                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            overlay.visibility = View.GONE
                        }
                        else -> {
                            overlay.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
            rvPlaylist.adapter = playlistAdapter
            playlistAdapter.onItemClickListener = PlaylistViewHolder.OnItemClickListener { playlist ->
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                viewModel.addTrackToPlaylist(track, playlist)
            }
        }
    }

    private fun showResultOfAddingTrack(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setButtonLikeResource(isFavourite: Boolean) {
        val resId = if (isFavourite) {
            R.drawable.ic_button_liked
        } else {
            R.drawable.ic_button_like
        }
        binding.buttonLike.setImageResource(resId)
    }

    private fun setPlaybackTime(timeMillis: Int) {
        binding.tvPlaybackProgress.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeMillis)
    }

    private fun changePlayButtonStyle(playStatus: PlayStatus) {
        val drawable = if (playStatus.isPlaying) {
            R.drawable.ic_button_pause
        } else {
            R.drawable.ic_button_play
        }
        binding.buttonPlay.setBackgroundResource(drawable)
    }

    companion object {
        private const val COVER_CORNER_RADIUS_IN_DP = 8f
    }
}