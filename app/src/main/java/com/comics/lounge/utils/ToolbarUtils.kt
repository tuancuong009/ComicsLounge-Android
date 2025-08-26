package com.comics.lounge.utils

import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.comics.lounge.R
import com.comics.lounge.databinding.ToolbarBinding
import com.squareup.picasso.Picasso

object ToolbarUtils {
    @JvmStatic
    fun Toolbar.showBackArrow(activity: AppCompatActivity) {
        val binding: ToolbarBinding = ToolbarBinding.inflate(activity.layoutInflater)
        binding.toolbarLogoLayout.visibility = View.GONE
        binding.llRightMenu.visibility = View.GONE
        binding.viewEmptySpace.visibility = View.VISIBLE
        setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        activity.setSupportActionBar(this)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    @JvmStatic
    fun Toolbar.loanAppLogo(ivLogo: ImageView) {
        Picasso.get()
                .load(R.drawable.comicslogo)
                .into(ivLogo)
    }
}

