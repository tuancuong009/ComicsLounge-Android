package com.comics.lounge.modals

import com.comics.lounge.modals.user.User
import java.util.Locale

data class GenericResponse(
        val status: String,
        val message: String?,

        /**
         * Used in forgot password
         */
        val user_id: String?,

        /**
         * Used in login response
         */
        val otp_email_status: String?,
        val mobile: String?,
        val otp_verified_status: String?,

        val params: User?
) {
    val statusInLower
        get() = status.lowercase(Locale.getDefault())
}