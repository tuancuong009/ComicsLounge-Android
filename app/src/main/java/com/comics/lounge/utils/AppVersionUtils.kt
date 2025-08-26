package com.comics.lounge.utils

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.conf.Constant
import com.comics.lounge.modals.AppVersion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

object AppVersionUtils {

    fun checkVersion(context: Context) {
        val retroApi = ComicsLoungeApp.getRetroApi()
        retroApi.checkVersion(Constant.DEVICE_TYPE, getBuildVersionName(context))
                .enqueue(object : Callback<AppVersion> {
                    override fun onFailure(call: Call<AppVersion>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<AppVersion>, response: Response<AppVersion>) {
                        if (!response.isSuccessful) return
                        showAppUpdate(context, response.body()!!)
                    }
                })

    }

    private fun showAppUpdate(context: Context, appVersion: AppVersion) {
        Timber.e("appVersion: $appVersion")
        if (appVersion.status != 1) return
        if (!appVersion.update) return

        DialogUtils.showUpdateAlert(context, context.getString(R.string.update_available),
                appVersion.message, appVersion.force_update == 1, DialogInterface.OnClickListener { dialog, which ->
            IntentUtils.openPlayStore(context)
        })
    }

    fun getBuildVersionName(context: Context): String {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

}