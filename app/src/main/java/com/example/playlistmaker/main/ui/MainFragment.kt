package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMainBinding
import com.example.playlistmaker.library.ui.MediaLibraryFragment
import com.example.playlistmaker.search.ui.SearchFragment
import com.example.playlistmaker.settings.ui.SettingsFragment

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = requireNotNull(_binding) { "Binding is null" }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                parentFragmentManager.commit {
                    replace(R.id.host_fragment_container, SearchFragment())
                    addToBackStack(null)
                }
            }
        }
        binding.buttonSearch.setOnClickListener(searchClickListener)
        binding.buttonLibrary.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.host_fragment_container, MediaLibraryFragment())
                addToBackStack(null)
            }
        }
        binding.buttonSettings.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.host_fragment_container, SettingsFragment())
                addToBackStack(null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}