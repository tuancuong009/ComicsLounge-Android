package com.comics.lounge.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.comics.lounge.BuildConfig


object IntentUtils {

    fun openPlayStore(context: Context) {
        val appId = BuildConfig.APPLICATION_ID
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")).apply {
                setPackage("com.android.vending")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (exception: ActivityNotFoundException) {
            context.startActivity(
                    Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appId")
                    )
            )
        }
    }
}