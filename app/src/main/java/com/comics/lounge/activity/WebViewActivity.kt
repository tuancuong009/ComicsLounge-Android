package com.comics.lounge.activity

import android.os.Bundle
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.databinding.ActivityWebviewBinding
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.utils.ToolbarUtils.showBackArrow


class WebViewActivity : AbstractBaseActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var retroApi: RetroApi
    lateinit var binding: ActivityWebviewBinding
    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_URL = "url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        retroApi = ComicsLoungeApp.getRetroApi()

        val url = intent.getStringExtra(EXTRA_URL)!!
        binding.tb.toolbarAppNameTxt.text = intent.getStringExtra(EXTRA_TITLE)?.let { it }
                ?: getString(R.string.video)
        binding.tb.toolbar.showBackArrow(this)

//        binding.webViewSuite.setCustomProgressBar(binding.progressBar)
//        binding.webViewSuite.startLoading(url)
    }

//    private fun canGoBack(): Boolean {
////        return binding.webViewSuite.goBackIfPossible()
//    }

//    override fun onBackPressed() {
//        if (!canGoBack()) {
//            super.onBackPressed()
//        }
//    }
}
