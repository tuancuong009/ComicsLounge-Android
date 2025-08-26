package com.comics.lounge.modals.user

import android.text.TextUtils
import com.comics.lounge.conf.Constant.UNVERIFIED
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("user_id")
    @Expose
    var userId: String,

    @SerializedName("otp_email_status")
    @Expose
    var otpEmailStatus: String? = UNVERIFIED,       //unverified

    @SerializedName("country_code")
    @Expose
    var countryCode: String? = null,

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null,

    @SerializedName("otp_verified_status")
    @Expose
    var otpVerifiedStatus: String? = UNVERIFIED,     //unverified

    @SerializedName("device_id")
    @Expose
    var deviceId: String? = null,

    @SerializedName("device_type")
    @Expose
    var deviceType: String? = null,

    @SerializedName("membership")
    @Expose
    var membership: Boolean? = null,

    @SerializedName("memership_id")
    @Expose
    var memershipId: String? = null,

    @SerializedName("membershipname")
    @Expose
    var membershipname: String? = null,

    @SerializedName("start_date")
    @Expose
    var startDate: String? = null,

    @SerializedName("end_date")
    @Expose
    var endDate: String? = null,

    @SerializedName("expire_status")
    @Expose
    var expireStatus: Boolean? = null,

    @SerializedName("expire_in_days")
    @Expose
    var expireInDays: Int? = null,

    @SerializedName("access_event")
    @Expose
    var accessEvent: Int? = null,

    @SerializedName("event_count_allowed")
    @Expose
    var eventCountAllowed: String? = null,

    @SerializedName("event_count_left")
    @Expose
    var eventCountLeft: Int? = null,

    @SerializedName("no_of_strike")
    @Expose
    var noOfStrike: Int? = null,

    @SerializedName("free_event_restored")
    @Expose
    var freeEventRestored: String,

    @SerializedName("activation_date")
    @Expose
    var activationDate: String? = null
) {
    val otpEmailStatusFinal
        get() = if (TextUtils.isEmpty(otpEmailStatus?.trim())) UNVERIFIED else otpEmailStatus

    val otpVerifiedStatusFinal
        get() = if (TextUtils.isEmpty(otpVerifiedStatus?.trim())) UNVERIFIED else otpVerifiedStatus

}