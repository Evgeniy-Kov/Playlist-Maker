package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.LocalResourcesRepository
import com.example.playlistmaker.sharing.domain.model.EmailData

class LocalResourcesRepositoryImpl(private val context: Context) : LocalResourcesRepository {
    override fun getShareAppLink(): String {
        return context.getString(R.string.share_link)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.support_email_address),
            context.getString(R.string.support_email_subject),
            context.getString(R.string.support_email_body)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.offer_link)
    }
}