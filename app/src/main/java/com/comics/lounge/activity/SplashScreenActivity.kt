package com.comics.lounge.activity

import android.app.TaskStackBuilder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import com.comics.lounge.R
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.conf.UrlCollection
import com.comics.lounge.servicecallback.ServiceCallback
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.webservice.manager.UserServiceManager
import com.squareup.picasso.Picasso
import io.intercom.android.sdk.Intercom
import timber.log.Timber

class SplashScreenActivity : AbstractBaseActivity(), ServiceCallback {

    private lateinit var sessionManager: SessionManager
    private lateinit var userServiceManager: UserServiceManager
    var video_view: VideoView? = null
    var ivLogo: ImageView? = null
    var iv_close: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sessionManager = SessionManager(this)
        ivLogo = findViewById(R.id.ivLogo)
        iv_close = findViewById(R.id.iv_close)
        video_view = findViewById(R.id.video_view)
        Picasso.get()
            .load(R.drawable.comicslogo)
            .into(ivLogo)
        openNextActivity()
        iv_close?.setOnClickListener { openNextActivity() }
    }

    private fun callingUpdateSession() {
        userServiceManager = UserServiceManager(this, this)
        userServiceManager.generateUrl(UrlCollection.USER + sessionManager.currentUser.userId)
        userServiceManager.prepareWebServiceJob()
        userServiceManager.featchData()
    }

    private fun userContinue() {
        if (!sessionManager.isCheckVideo) {
            ivLogo?.visibility = View.GONE
            iv_close?.visibility = View.VISIBLE
            video_view?.visibility = View.VISIBLE
//            video_view!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.vid_intro))
            video_view!!.requestFocus()
            video_view!!.start()
            video_view!!.setOnCompletionListener { openNextActivity() }
        } else {
            openNextActivity()
        }

    }

    private fun openNextActivity() {
        ivLogo?.visibility = View.VISIBLE
        iv_close?.visibility = View.GONE
        video_view?.visibility = View.GONE
        sessionManager.checkVideo()
        val handler = Handler()
        handler.postDelayed({
            if (GlobalConf.checkInternetConnection(applicationContext)) {
                if (sessionManager.isLoggedIn) {
                    callingUpdateSession()
                } else {
                    val intent = Intent(applicationContext, RegistrationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(applicationContext, NoInternetActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1500)


    }


    override fun serviceStarted(msg: String, serviceName: String) {}
    override fun serviceEnd(msg: String, serviceName: String) {
        //FixMe: java.lang.IllegalStateException: userServiceManager.serviceStatus must not be null
        try {
            if (userServiceManager.serviceStatus.lowercase() == Constant.SUCCESS) {

                val currentUserTemp = sessionManager.currentUser
                sessionManager.createOrUpdateLogin(userServiceManager.userObj)

                val currentUser = sessionManager.currentUser
                currentUser.otpEmailStatus = currentUserTemp.otpEmailStatusFinal
                currentUser.otpVerifiedStatus = currentUserTemp.otpVerifiedStatusFinal
                sessionManager.createOrUpdateLogin(currentUser)
//                sessionManager.freeEventRestored(currentUser.freeEventRestored,currentUser.eventCountAllowed!!,
//                    currentUser.freeEventRestored)
                sessionManager.freeEventRestored(currentUser.freeEventRestored,currentUser.eventCountAllowed!!,
                    currentUser.eventCountLeft.toString())

                //User user = sessionManager.getCurrentUser();
                Timber.e(sessionManager.currentUser.membership.toString())

                val intent = Intent(applicationContext, Home::class.java)
                intent.putExtra("isSplshScreen", 1)
                startActivity(intent)
                finish()
            } else logout()
        } catch (e: Exception) {
            logout()
        }
    }

    private fun logout() {
        sessionManager.logoutUser()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        finish()
    }

    override fun serviceInProgress(msg: String, serviceName: String) {}
}