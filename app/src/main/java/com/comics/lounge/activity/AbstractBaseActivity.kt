package com.comics.lounge.activity

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Rect
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.comics.lounge.databinding.PopupMemberVideoBinding
import com.comics.lounge.utils.AppUtil


abstract class AbstractBaseActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private var focusedViewOnActionDown: View? = null
    private var touchWasInsideFocusedView = false

    //Close keyboard on touch outside of edit text
    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                focusedViewOnActionDown = currentFocus
                if (focusedViewOnActionDown != null) {
                    val rect = Rect()
                    val coordinates = IntArray(2)
                    focusedViewOnActionDown!!.getLocationOnScreen(coordinates)
                    rect.set(
                        coordinates[0], coordinates[1],
                        coordinates[0] + focusedViewOnActionDown!!.width,
                        coordinates[1] + focusedViewOnActionDown!!.height
                    )
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    touchWasInsideFocusedView = rect.contains(x, y)
                }
            }
            MotionEvent.ACTION_UP -> if (focusedViewOnActionDown != null) {
                // dispatch to allow new view to (potentially) take focus
                val consumed = super.dispatchTouchEvent(motionEvent)
                val currentFocus = currentFocus

                // if the focus is still on the original view and the touch was inside that view,
                // leave the keyboard open.  Otherwise, if the focus is now on another view and that view
                // is an EditText, also leave the keyboard open.
                if (currentFocus == focusedViewOnActionDown) {
                    if (touchWasInsideFocusedView) {
                        return consumed
                    }
                } else if (currentFocus is EditText) {
                    return consumed
                }

                // the touch was outside the originally focused view and not inside another EditText,
                // so close the keyboard
                AppUtil.hideKeyboard(focusedViewOnActionDown!!)
                focusedViewOnActionDown!!.clearFocus()
                return consumed
            }
        }
        return super.dispatchTouchEvent(motionEvent)
    }

    // popup membership video
    fun popupVideo() {
        val dialog = Dialog(this)
        val videoBinding: PopupMemberVideoBinding = PopupMemberVideoBinding.inflate(layoutInflater)
        dialog.setContentView(videoBinding.getRoot())
        AppUtil.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT)
        videoBinding.videoView.getSettings().setJavaScriptEnabled(true);
        videoBinding.videoView.getSettings().setMediaPlaybackRequiresUserGesture(false) // allow autoplay
        videoBinding.videoView.setWebChromeClient(WebChromeClient())
        val videoId = "gw_saalfEu8"
        val html = ("<html><body style='margin:0;'>"
                + "<iframe width='100%' height='100%' style='border:0;' "
                + "src='https://www.youtube-nocookie.com/embed/" + videoId + "?autoplay=1&controls=0&showinfo=0&rel=0&modestbranding=1&playsinline=1' "
                + "frameborder='0' allow='autoplay; fullscreen' allowfullscreen></iframe>"
                + "</body></html>")

        videoBinding.videoView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        videoBinding.btClose.setOnClickListener { v ->

            dialog.dismiss()
        }
        dialog.setOnDismissListener { dialog1: DialogInterface? ->

        }
        dialog.show()
    }
}