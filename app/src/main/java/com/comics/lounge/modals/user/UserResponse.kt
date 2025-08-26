package com.comics.lounge.modals.user

import com.comics.lounge.conf.Constant.UNVERIFIED
import java.util.Locale

data class UserResponse(
        private val status: String,
       // val message: String?,

        val otp_email_status: String = UNVERIFIED,
        val mobile: Boolean,
        val otp_verified_status: String = UNVERIFIED,
        val balance: Double,

        private val params: User?
) {
    val statusInLower
        get() = status.lowercase(Locale.getDefault())

    val user
        get() = params?.apply {
            otpEmailStatus = otp_email_status
            otpVerifiedStatus = otp_verified_status
        }
}