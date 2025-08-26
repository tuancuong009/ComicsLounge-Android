package com.comics.lounge.sessionmanager

import android.content.Context
import android.content.SharedPreferences
import com.comics.lounge.modals.user.User
import com.google.gson.Gson
import org.json.JSONObject

/**
 * Created by GAURAV on 02-Jan-'20.
 */
class SessionManager(var context: Context) {

    var editor: SharedPreferences.Editor

    // pref mode
    private var PRIVATE_MODE = 0

    private val sharedPreferences: SharedPreferences

    fun createOrUpdateLogin(user: JSONObject) {
        //storing login value true
        editor.putString(USER_JSON_STR, user.toString())

        // commit changes
        editor.commit()
    }

    fun createOrUpdateLogin(user: User) {
        val gson = Gson()
        val json = gson.toJson(user, User::class.java)

        //storing login value true
        editor.putString(USER_JSON_STR, json)

        // commit changes
        editor.commit()
    }

    val currentUser
        get() = run {
            val gson = Gson()
            val json = sharedPreferences.getString(USER_JSON_STR, null)
            gson.fromJson(json, User::class.java)!!
        }

    val currentlyLoggedUserId: String
        get() = currentUser.userId

    fun storeFreeTicketCount(freeTicketCount: Int) {
        editor.putInt(FREE_TICKET, freeTicketCount)

        // commit changes
        editor.commit()
    }

    val totalFreeTickets: Int
        get() = sharedPreferences.getInt(FREE_TICKET, 0)




    fun freeEventRestored(freeTicketCount: String?,event_count_allowed: String?,event_count_left: String?) {
        editor.putString(FREE_EVENT_RESTORED, freeTicketCount?:"0")
        editor.putString(EVENT_COUNT_ALLOWED, event_count_allowed?:"0")
        editor.putString(EVENT_COUNT_LEFT, event_count_left?:"0")
        editor.commit()
    }

    val freeEventRestoredCount: String
        get() = sharedPreferences.getString(FREE_EVENT_RESTORED, "0").toString()

    val eventCountAllowed: String
        get() = sharedPreferences.getString(EVENT_COUNT_ALLOWED, "0").toString()

    val eventCountLeft: String
        get() = sharedPreferences.getString(EVENT_COUNT_LEFT, "0").toString()

    /*
     ** clear session detail
     */
    fun logoutUser() {
        editor.clear()
        editor.remove(PREF_NAME)
        editor.remove(USER_JSON_STR)
        editor.commit()
    }


    fun checkVideo() {
        editor.putBoolean(USER_VIDEO, true)
        editor.apply()
    }

    val isCheckVideo: Boolean
        get() = sharedPreferences.getBoolean(USER_VIDEO, false)

    /*
     ** Quick check login
     */
    // get login state
    val isLoggedIn: Boolean
        get() = sharedPreferences.getString(USER_JSON_STR, null) != null

    var showOffer: Boolean
        get() = sharedPreferences.getBoolean(SHOW_OFFER, true)
        set(value) {
            editor.putBoolean(SHOW_OFFER, value); editor.commit()
        }

    companion object {
        // pref file name
        private const val PREF_NAME = "ComicAppPreference"

        // All Shared Preferences Keys
        private const val USER_VIDEO = "userVideo"
        private const val USER_JSON_STR = "userJsonString"
        private const val FREE_TICKET = "free_ticekt"
        private const val FREE_EVENT_RESTORED = "free_event_restored"
        private const val EVENT_COUNT_ALLOWED = "event_count_allowed"
        private const val EVENT_COUNT_LEFT = "event_count_left"
        private const val SHOW_OFFER = "show_offer"
    }

    //User data (make variable public to access from outside)
    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }
}
