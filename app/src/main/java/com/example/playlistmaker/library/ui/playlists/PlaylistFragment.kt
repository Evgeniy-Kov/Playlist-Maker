package com.example.playlistmaker.library.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val bottomSheetBehavior by lazy {
        BottomSheetBehavior.from(binding.playlistsBottomSheet)
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


        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                val screenHeight = binding.main.height
                val playlistDescriptionHeight = binding.constraint.height

                bottomSheetBehavior.setPeekHeight(screenHeight - playlistDescriptionHeight)
                binding.constraint.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        binding.constraint.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

}
