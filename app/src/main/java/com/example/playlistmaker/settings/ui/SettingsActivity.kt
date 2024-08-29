package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.common.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.domain.model.DarkThemeMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
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
            (applicationContext as App).switchTheme(darkThemeMode)
            viewModel.saveDarkThemeMode(darkThemeMode)
        }
    }
}