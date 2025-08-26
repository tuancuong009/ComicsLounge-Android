package com.comics.lounge.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.databinding.ActivityLoginBinding
import com.comics.lounge.modals.user.UserResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.utils.AppUtil
import com.comics.lounge.utils.DialogUtils
import com.comics.lounge.utils.UserUtils
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AbstractBaseActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var retroApi: RetroApi
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        retroApi = ComicsLoungeApp.getRetroApi()

        binding.tvSignup.setOnClickListener {
            if (intent.getStringExtra("from") != null){
                finish()
            }else{
                startActivity(Intent(this, RegistrationActivity::class.java).putExtra("from", "auth"))
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(applicationContext, ForgotPasswordActivity::class.java))
        }
        binding.btnSubmit.setOnClickListener { onLogin() }
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
        binding.passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    binding.ipPw.error = null
                    binding.passwordEdit.setBackgroundResource(R.drawable.bg_edt)
                    binding.tvPwLabel.setTextColor(getColor(R.color.gray_1))
                }
            }
        })
        binding.btBack.setOnClickListener {
            finish()
        }
    }

    private fun onLogin() {
        AppUtil.hideKeyboard(binding.llMain)
        if (!GlobalConf.checkInternetConnection(this)) {
            Snackbar.make(binding.llMain,
                    resources.getString(R.string.internet_not_found_str),
                    Snackbar.LENGTH_LONG).show()
            return
        }
        if (!validate) return
        showDialog()

        retroApi.login(hashMapOf(
                "email" to binding.emailEdit.text.toString().trim(),
                "password" to binding.passwordEdit.text.toString().trim(),
                "device_id" to GlobalConf.getUniqueID(),
                "device_type" to Constant.DEVICE_TYPE
        )).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                handleSuccess(response)
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                dismissDialog()
                Log.e("tag","error__"+t.toString())
                DialogUtils.showInfoAlert(this@LoginActivity,
                        getString(R.string.server_error),
                        t.message)
            }
        })
    }

    private fun handleSuccess(response: Response<UserResponse>) {
        if (!response.isSuccessful) {
            DialogUtils.showInfoAlert(this,
                    getString(R.string.server_error),
                    response.errorBody().toString())
            return
        }

        val userResponse = response.body()!!

        if (userResponse.statusInLower == Constant.SUCCESS) {

            Handler().postDelayed({
                onSuccess(userResponse)
            }, 300)

        } else {
            displaySnackBarMessage(userResponse.statusInLower)
        }
    }

    private fun onSuccess(userResponse: UserResponse) {
        //Update user session
        fetchAndUpdateUser(retroApi, sessionManager, userResponse.user!!.userId)
//        sessionManager.createOrUpdateLogin(userResponse.user!!)
    }

    private val validate
        get() = when {
            binding.emailEdit.text.toString().trim().isEmpty() -> {
                binding.ipEmail.error = getString(R.string.this_field_is_required)
                binding.emailEdit.setBackgroundResource(R.drawable.bg_edt_error)
                binding.tvEmailLabel.setTextColor(getColor(R.color.red))
                false
            }
            binding.passwordEdit.text.toString().trim().isEmpty() -> {
                binding.ipPw.error = getString(R.string.this_field_is_required)
                binding.tvPwLabel.setTextColor(getColor(R.color.red))
                false
            }
            !AppUtil.isValidEmail(binding.emailEdit.text.toString().trim()) -> {
                binding.ipEmail.error = getString(R.string.enter_valid_email)
                binding.tvEmailLabel.setTextColor(getColor(R.color.red))
                false
            }
            /*
            //TODO: removed due to pwd can be not validated
            !AppUtil.isValidPwd(edtPassword.text.toString().trim()) -> {
                edtPassword.error = getString(R.string.password_validation)
                edtPassword.requestFocus()
                false
            }*/
            else -> true
        }

    private fun showDialog() {
        ComicsLoungeApp.dialogUtils.show(this)
    }

    private fun dismissDialog() {
        ComicsLoungeApp.dialogUtils.hide()
    }

    private fun displaySnackBarMessage(message: String) {
        Snackbar.make(binding.llMain, message, Snackbar.LENGTH_LONG).show()
    }

    fun fetchAndUpdateUser(
        retroApi: RetroApi, sessionManager: SessionManager, userId: String,
        onError: ((String?) -> Unit)? = null,
        onSuccess: ((UserResponse?) -> Unit)? = null
    ) {
        retroApi.fetchUser(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                dismissDialog()
                if (!response.isSuccessful) return
                val userResponse = response.body()!!
                if (userResponse.statusInLower == Constant.SUCCESS) {
                    sessionManager.createOrUpdateLogin(userResponse.user!!)
                    sessionManager.freeEventRestored(
                        userResponse.user!!.freeEventRestored,
                        userResponse.user!!.eventCountAllowed,
                        //     userResponse.user!!.freeEventRestored
                        userResponse.user!!.eventCountLeft.toString()
                    )
                    AppUtil.setPw(this@LoginActivity, binding.passwordEdit.text.toString().trim())
                    var intent = Intent(applicationContext, Home::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    onSuccess?.invoke(userResponse)
                } else {
                    onError?.invoke(userResponse.statusInLower)
                }

            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                t.printStackTrace()
                onError?.invoke(t.message)
            }
        })
    }
}