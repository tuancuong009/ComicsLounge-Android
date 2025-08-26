package com.comics.lounge.utils

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.conf.Constant
import com.comics.lounge.modals.AppVersion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

object PasswordFieldVisibleOrHide {
    fun onPasswordVisibleOrHide(edt: EditText, img: ImageView, isHideOrVisible: Boolean) {
        if (isHideOrVisible) {
            edt.inputType = InputType.TYPE_CLASS_TEXT
            edt.setSelection(edt.length())
            img.setImageResource(R.drawable.ic_hide_eye)
        } else {
            edt.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
            edt.setSelection(edt.length())
            img.setImageResource(R.drawable.ic_eye_seen)
        }
    }
}