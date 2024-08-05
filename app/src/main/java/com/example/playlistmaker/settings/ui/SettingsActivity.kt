package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.common.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.domain.model.DarkThemeMode

class SettingsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    private val saveDarkThemeModeUseCase = Creator.provideSaveDarkThemeModeUseCase()

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
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)

            startActivity(shareIntent)
        }

        binding.tvSupport.setOnClickListener {
            val supportIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_address)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_body))
            }

            startActivity(supportIntent)
        }

        binding.tvUserAgreement.setOnClickListener {
            val agreementIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.offer_link))
            }

            startActivity(agreementIntent)
        }

        binding.switchTheme.setOnCheckedChangeListener { switcher, isChecked ->
            val darkThemeMode = if (isChecked) DarkThemeMode.ON else DarkThemeMode.OFF
            (applicationContext as App).switchTheme(darkThemeMode)
            saveDarkThemeModeUseCase(darkThemeMode)
        }
    }
}