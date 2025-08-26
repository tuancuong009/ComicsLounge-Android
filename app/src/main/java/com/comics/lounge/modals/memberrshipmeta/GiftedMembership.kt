package com.comics.lounge.modals.memberrshipmeta

data class GiftedMembership(
        val membership: Boolean,
        val user_id: Int,
        val payer_user_id: Int,
        val memership_id: Int,
        val membershipname: String,
        val phone: String,
        val email: String,
        val transaction_id: String,
        val start_date: String,
        val end_date: String,
        val expire_status: String,
        val expire_in_days: Int
)