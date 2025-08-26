package com.comics.lounge.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.activity.EmailOTPVerifyActivity.Companion.EXTRA_SCREEN_FLOW
import com.comics.lounge.activity.EmailOTPVerifyActivity.Companion.EXTRA_USER_EMAIL
import com.comics.lounge.activity.EmailOTPVerifyActivity.Companion.EXTRA_USER_ID
import com.comics.lounge.activity.EmailOTPVerifyActivity.ScreenFlow.FORGOT
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.databinding.ActivityPasswordForgotBinding
import com.comics.lounge.modals.GenericResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.utils.AppUtil
import com.comics.lounge.utils.DialogUtils
import com.comics.lounge.utils.ToolbarUtils.loanAppLogo
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AbstractBaseActivity() {

    lateinit var retroApi: RetroApi
    lateinit var binding: ActivityPasswordForgotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retroApi = ComicsLoungeApp.getRetroApi()
        binding.btnReset.setOnClickListener { onSubmit() }
        binding.emailEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    binding.ipEmail.error = null
                    binding.emailEdit.setBackgroundResource(R.drawable.bg_edt)
                    binding.tvEmailLabel.setTextColor(getColor(R.color.gray_1))
                }
            }
        })
        binding.btBack.setOnClickListener{
            finish()
        }
    }

    private fun onSubmit() {
        AppUtil.hideKeyboard(binding.llMain)
        if (!GlobalConf.checkInternetConnection(this)) {
            Snackbar.make(binding.llMain,
                    resources.getString(R.string.internet_not_found_str),
                    Snackbar.LENGTH_LONG).show()
            return
        }
        if (!validate) return
        showDialog()

        retroApi.forgotEmailOtpSend(hashMapOf(
                "email" to binding.emailEdit.text.toString().trim()
        )).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                handleSuccess(response)
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                dismissDialog()
                DialogUtils.showInfoAlert(this@ForgotPasswordActivity,
                        getString(R.string.server_error),
                        t.message)
            }
        })
    }

    private fun handleSuccess(response: Response<GenericResponse>) {
        dismissDialog()
        if (!response.isSuccessful) {
            DialogUtils.showInfoAlert(this,
                    getString(R.string.server_error),
                    response.errorBody().toString())
            return
        }

        val genericResponse = response.body()!!
        if (genericResponse.statusInLower == Constant.SUCCESS) {
            startActivity(Intent(this, EmailOTPVerifyActivity::class.java).apply {
                putExtra(EXTRA_SCREEN_FLOW, FORGOT)
                putExtra(EXTRA_USER_ID, genericResponse.user_id!!)
                putExtra(EXTRA_USER_EMAIL, binding.emailEdit.text.toString())
            })

        } else {
            Toast.makeText(this, genericResponse.message
                    ?: getString(R.string.email_sent_invalid), Toast.LENGTH_LONG).show()
        }
    }

    private val validate
        get() = when {
            binding.emailEdit.text.toString().trim().isEmpty() -> {
                binding.ipEmail.error = getString(R.string.this_field_is_required)
                binding.emailEdit.setBackgroundResource(R.drawable.bg_edt_error)
                binding.tvEmailLabel.setTextColor(getColor(R.color.red))
                false
            }
            !AppUtil.isValidEmail(binding.emailEdit.text.toString().trim()) -> {
                binding.ipEmail.error = getString(R.string.enter_valid_email)
                binding.emailEdit.setBackgroundResource(R.drawable.bg_edt_error)
                binding.tvEmailLabel.setTextColor(getColor(R.color.red))
                false
            }
            else -> true
        }

    private fun showDialog() {
        ComicsLoungeApp.dialogUtils.show(this)
    }

    private fun dismissDialog() {
        ComicsLoungeApp.dialogUtils.hide()
    }
}