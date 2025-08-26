package com.comics.lounge.modals

data class AppVersion(
        val status: Int,
        val update: Boolean,
        val message: String,
        val force_update: Int
)