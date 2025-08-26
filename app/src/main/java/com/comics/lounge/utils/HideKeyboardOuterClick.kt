package com.comics.lounge.utils

import android.view.View
import android.view.ViewGroup
import android.widget.EditText

object HideKeyboardOuterClick {
    fun hideKeyBoardOuterClick(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                AppUtil.hideKeyboard(view)
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                hideKeyBoardOuterClick(innerView)
            }
        }
    }
}