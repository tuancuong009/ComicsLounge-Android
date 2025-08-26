package com.comics.lounge.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.activity.EmailOTPVerifyActivity.ScreenFlow.FORGOT
import com.comics.lounge.activity.EmailOTPVerifyActivity.ScreenFlow.HOME
import com.comics.lounge.activity.EmailOTPVerifyActivity.ScreenFlow.REGISTER
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.databinding.ActivityEmailOtpVerifyBinding
import com.comics.lounge.modals.GenericResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.utils.AppUtil
import com.comics.lounge.utils.DialogUtils
import com.google.android.material.snackbar.Snackbar
import com.ozcanalasalvar.otp_view.view.OtpView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmailOTPVerifyActivity : AbstractBaseActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var retroApi: RetroApi
    lateinit var screenFlow: ScreenFlow
    lateinit var userId: String
    lateinit var email: String
    lateinit var binding: ActivityEmailOtpVerifyBinding
    var otpCode : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        retroApi = ComicsLoungeApp.getRetroApi()

        binding.tvResentOTP.setOnClickListener { resendMail() }
        binding.btnSubmit.setOnClickListener { onVerifyOTP() }
        binding.btnHome.setOnClickListener{
            navigateToSignIn()
        }

        binding.edtOtp.setTextChangeListener(object : OtpView.ChangeListener{
            override fun onTextChange(value: String, completed: Boolean) {
                otpCode = if (completed){
                    value
                } else {
                    ""
                }
            }
        })

        if (intent.hasExtra(EXTRA_SCREEN_FLOW)) {
            screenFlow = intent.getSerializableExtra(EXTRA_SCREEN_FLOW) as ScreenFlow
            userId = intent.getStringExtra(EXTRA_USER_ID)!!
            email = intent.getStringExtra(EXTRA_USER_EMAIL)!!
            binding.tvConfirmationTitle.text = getString(R.string.enter_code_email, email)
        } else finish()
    }
    private fun navigateToSignIn() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun onVerifyOTP() {
        AppUtil.hideKeyboard(binding.llMain)
        if (!GlobalConf.checkInternetConnection(this)) {
            Snackbar.make(binding.llMain,
                    resources.getString(R.string.internet_not_found_str),
                    Snackbar.LENGTH_LONG).show()
            return
        }
        if (!validate) return
        showDialog()

        val callBack = when (screenFlow) {
            FORGOT -> retroApi.forgotEmailOtpVerify(hashMapOf(
                    "user_id" to userId,
                    "otp_code" to otpCode)
            )
            HOME, REGISTER -> retroApi.emailOtpVerify(hashMapOf(
                    "user_id" to userId,
                    "otp_code" to otpCode,
                    "timestamp" to System.currentTimeMillis().toString())
            )
        }

        callBack.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                handleSuccess(response)
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                dismissDialog()
                DialogUtils.showInfoAlert(this@EmailOTPVerifyActivity,
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
            Toast.makeText(this, R.string.email_otp_verified, Toast.LENGTH_LONG).show()
            manageScreenFlow(genericResponse)
        } else {
            Toast.makeText(this, genericResponse.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun manageScreenFlow(genericResponse: GenericResponse?) {
        when (screenFlow) {
            FORGOT -> {
                startActivity(Intent(this, ResetPasswordActivity::class.java).apply {
                    putExtra(EXTRA_USER_ID, genericResponse!!.user_id!!)
                })
                finish()
            }
            HOME -> {
                sessionManager.createOrUpdateLogin(sessionManager.currentUser.apply {
                    otpEmailStatus = Constant.VERIFIED
                })
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            REGISTER -> {
                val intent = Intent(getApplicationContext(), LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun resendMail() {
        AppUtil.hideKeyboard(binding.llMain)
        if (!GlobalConf.checkInternetConnection(this)) {
            Snackbar.make(binding.llMain,
                    resources.getString(R.string.internet_not_found_str),
                    Snackbar.LENGTH_LONG).show()
            return
        }
        showDialog()

        val callBack = when (screenFlow) {
            FORGOT -> retroApi.forgotEmailOtpSend(hashMapOf(
                    "email" to email
            ))
            HOME, REGISTER -> retroApi.otpSendAll(hashMapOf(
                    "user_id" to userId,
                    "otp_type" to "email"
            ))
        }
        callBack.enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                dismissDialog()
                if (!response.isSuccessful) {
                    DialogUtils.showInfoAlert(this@EmailOTPVerifyActivity,
                            getString(R.string.server_error),
                            response.errorBody().toString())
                    return
                }

                val genericResponse = response.body()!!
                if (genericResponse.statusInLower == Constant.SUCCESS) {
                    Toast.makeText(this@EmailOTPVerifyActivity, genericResponse.message
                            ?: getString(R.string.email_sent), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@EmailOTPVerifyActivity, genericResponse.message
                            ?: getString(R.string.email_sent_invalid), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                dismissDialog()
                DialogUtils.showInfoAlert(this@EmailOTPVerifyActivity,
                        getString(R.string.server_error),
                        t.message)
            }
        })
    }

    private val validate
        get() = when {
            otpCode.isEmpty() -> {
                Toast.makeText(this, getString(R.string.otp_validate), Toast.LENGTH_SHORT).show()
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

    companion object {
        const val EXTRA_SCREEN_FLOW = "screen_flow"
        const val EXTRA_USER_ID = "user_id"
        const val EXTRA_USER_EMAIL = "email"
    }

    enum class ScreenFlow {
        FORGOT, HOME, REGISTER
    }
}

