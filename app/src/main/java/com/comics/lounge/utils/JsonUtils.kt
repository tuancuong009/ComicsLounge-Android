package com.comics.lounge.utils

import android.content.Context
import com.comics.lounge.conf.Constant
import org.json.JSONException
import org.json.JSONObject

object JsonUtils {
    fun buildPayPalLogJson(context: Context, userID: String, logText: String): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", userID)
            jsonObject.put("log_text", logText)
            jsonObject.put("app_type", Constant.DEVICE_TYPE)
            jsonObject.put("app_version", AppUtil.getBuildVersionCode(context))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }
}