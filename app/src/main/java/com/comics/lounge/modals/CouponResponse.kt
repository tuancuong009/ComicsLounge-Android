package com.comics.lounge.modals

import java.util.Locale

data class CouponResponse(
        private val status: String,
        val param: Coupon?,
        val message: String?
) {
    val statusInLower
        get() = status.lowercase(Locale.getDefault())

    inner class Coupon(
            val id: Int,
            val discount: Float,
            val discount_type: String,
            val message: String?
    )
}

enum class DiscountType(val type: String) {
    Amount("amount"),
    Percentage("percentage")
}
