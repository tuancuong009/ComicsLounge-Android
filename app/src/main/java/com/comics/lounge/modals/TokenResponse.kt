package com.comics.lounge.modals

import java.util.Locale

data class TokenResponse(
        private val status: String,
        val token: String?
) {
    val statusInLower
        get() = status.lowercase(Locale.getDefault())
}