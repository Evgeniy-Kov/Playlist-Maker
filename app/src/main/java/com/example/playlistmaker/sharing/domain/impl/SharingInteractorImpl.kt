package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.R
import com.example.playlistmaker.common.App
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return App.getStringFromResources(R.string.share_link)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            App.getStringFromResources(R.string.support_email_address),
            App.getStringFromResources(R.string.support_email_subject),
            App.getStringFromResources(R.string.support_email_body)
        )
    }

    private fun getTermsLink(): String {
        return App.getStringFromResources(R.string.offer_link)
    }
}