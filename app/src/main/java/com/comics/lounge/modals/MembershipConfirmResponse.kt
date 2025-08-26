package com.comics.lounge.modals

import com.comics.lounge.modals.user.User
import java.util.Locale

data class MembershipConfirmResponse(
        private val status: String,
        val message: String?


) {
    val statusInLower
        get() = status.lowercase(Locale.getDefault())
}