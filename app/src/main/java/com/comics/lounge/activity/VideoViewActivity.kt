package com.comics.lounge.activity

import android.net.Uri
import android.os.Bundle
import com.comics.lounge.R
import com.comics.lounge.databinding.ActivityVideoBinding
import com.comics.lounge.utils.ToolbarUtils.showBackArrow


class VideoViewActivity : AbstractBaseActivity() {
    lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tb.toolbar.showBackArrow(this)

        binding.videoView.setVideoURI(Uri.parse("https://www.youtube.com/embed/gw_saalfEu8?si=3tUQg08LFmWqXMik"))
        binding.videoView.start()

    }

}
