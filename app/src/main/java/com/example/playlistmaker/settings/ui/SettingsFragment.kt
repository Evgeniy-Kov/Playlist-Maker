package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.common.App
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.domain.model.DarkThemeMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = requireNotNull(_binding) { "Binding is null" }

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvShare.setOnClickListener {
            viewModel.shareApp()
        }

        binding.tvSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.tvUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }

        binding.switchTheme.setOnCheckedChangeListener { switcher, isChecked ->
            val darkThemeMode = if (isChecked) DarkThemeMode.ON else DarkThemeMode.OFF
            (requireContext().applicationContext as App).switchTheme(darkThemeMode)
            viewModel.saveDarkThemeMode(darkThemeMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

