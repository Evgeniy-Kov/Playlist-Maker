package com.example.playlistmaker.library.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.medialibrary.MediaLibraryFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    protected val binding get() = _binding!!

    private val playlistAdapter = PlaylistAdapter()

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNewPlaylist.setOnClickListener {
            val direction = MediaLibraryFragmentDirections.actionMediaLibraryFragmentToNewPlaylistFragment()
            findNavController().navigate(direction)

        }



        viewModel.screenStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {

                is PlaylistsFragmentState.Content -> {
                    showContent(state.playlists)
                }
                PlaylistsFragmentState.Empty -> {
                    showPlaceholder()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            tvNothingFound.isVisible = false
            rvPlaylists.isVisible = true
            rvPlaylists.adapter = playlistAdapter
            playlistAdapter.playlistList = playlists
        }
    }

    private fun showPlaceholder() {
        binding.apply {
            tvNothingFound.isVisible = true
            rvPlaylists.isVisible = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PlaylistsFragment()
    }
}