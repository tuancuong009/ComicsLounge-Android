package com.comics.lounge.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.comics.lounge.R
import com.comics.lounge.activity.NewMain
import com.comics.lounge.activity.TermCondition
import com.comics.lounge.activity.WebViewActivity
import com.comics.lounge.conf.UrlCollection
import com.comics.lounge.fragments.FrmTerm

object Utils {
    fun generateClickableLink(context: Context, textView: TextView) {
        val spannableString = SpannableString(context.getString(R.string.reg_agree_policy))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(context, TermCondition::class.java)
                intent.putExtra(WebViewActivity.EXTRA_TITLE, context.getString(R.string.terms_condition_str))
                intent.putExtra(WebViewActivity.EXTRA_URL, UrlCollection.TEMS_CON_URL)
                context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#EC027D")
            }
        }
        spannableString.setSpan(clickableSpan, spannableString.length - 13,
                spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun generateClickableLinkError(context: Context, textView: TextView) {
        val spannableString = SpannableString(context.getString(R.string.reg_agree_policy))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra(WebViewActivity.EXTRA_TITLE, context.getString(R.string.terms_condition_str))
                intent.putExtra(WebViewActivity.EXTRA_URL, UrlCollection.TEMS_CON_URL)
                context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#FF0000")
            }
        }
        spannableString.setSpan(clickableSpan, spannableString.length - 13,
            spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun generateClickableLinkToFrm(context: NewMain, textView: TextView) {
        val spannableString = SpannableString(context.getString(R.string.reg_agree_policy))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                context.addFrmDetail(FrmTerm())
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = Color.parseColor("#EC027D")
            }
        }
        spannableString.setSpan(clickableSpan, spannableString.length - 13,
            spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}