package com.comics.lounge.modals.memberrshipmeta

data class SharedMembership(
        val memership_id: Int,
        val membershipname: String,
        val description:String,
        val price: String,
        val other_price: String,
        val image: String,
        val create_on: String,
        val modify_on: String,
        val expire_status: String,
        val expire_in_days: String
)