package com.comics.lounge.utils

import com.comics.lounge.conf.Constant
import com.comics.lounge.modals.user.UserResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserUtils {
    fun fetchAndUpdateUser(
        retroApi: RetroApi, sessionManager: SessionManager, userId: String,
        onError: ((String?) -> Unit)? = null,
        onSuccess: ((UserResponse?) -> Unit)? = null
    ) {
        retroApi.fetchUser(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (!response.isSuccessful) return
                val userResponse = response.body()!!
                if (userResponse.statusInLower == Constant.SUCCESS) {
                    sessionManager.createOrUpdateLogin(userResponse.user!!)
                    sessionManager.freeEventRestored(
                        userResponse.user!!.freeEventRestored,
                        userResponse.user!!.eventCountAllowed,
                   //     userResponse.user!!.freeEventRestored
                             userResponse.user!!.eventCountLeft.toString()
                    )
                    onSuccess?.invoke(userResponse)
                } else {
                    onError?.invoke(userResponse.statusInLower)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                t.printStackTrace()
                onError?.invoke(t.message)
            }
        })
    }
}