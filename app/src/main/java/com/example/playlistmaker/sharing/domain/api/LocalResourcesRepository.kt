package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.sharing.domain.model.EmailData

interface LocalResourcesRepository {

    fun getShareAppLink(): String

    fun getSupportEmailData(): EmailData

    fun getTermsLink(): String
}