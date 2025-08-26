package com.comics.lounge.activity

import android.os.Bundle
import android.widget.Toast
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.ComicsLoungeApp.dialogUtils
import com.comics.lounge.R
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.databinding.ActivityPasswordChangeBinding
import com.comics.lounge.modals.GenericResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.utils.AppUtil
import com.comics.lounge.utils.DialogUtils
import com.comics.lounge.utils.PasswordFieldVisibleOrHide
import com.comics.lounge.utils.ToolbarUtils.showBackArrow
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangePasswordActivity : AbstractBaseActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var retroApi: RetroApi
    lateinit var binding: ActivityPasswordChangeBinding
    var newPassword: Boolean = true
    var reTypePassword: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_password_change)

        sessionManager = SessionManager(this)
        retroApi = ComicsLoungeApp.getRetroApi()

        binding.toolbar.toolbarAppNameTxt.setText(R.string.change_pwd)
        binding.toolbar.toolbar.showBackArrow(this)

        binding.btnSubmit.setOnClickListener { onChangePassword() }

        binding.imgNewPasswordEye.setOnClickListener {
            newPassword = if (newPassword) {
                PasswordFieldVisibleOrHide.onPasswordVisibleOrHide(binding.edtNewPwd, binding.imgNewPasswordEye, newPassword)
                false
            } else {
                PasswordFieldVisibleOrHide.onPasswordVisibleOrHide(binding.edtNewPwd, binding.imgNewPasswordEye, newPassword)
                true
            }
        }

        binding.imgRePasswordEye.setOnClickListener {
            reTypePassword = if (reTypePassword) {
                PasswordFieldVisibleOrHide.onPasswordVisibleOrHide(binding.edtReNewPwd, binding.imgRePasswordEye, true)
                false
            } else {
                PasswordFieldVisibleOrHide.onPasswordVisibleOrHide(binding.edtReNewPwd, binding.imgRePasswordEye, false)
                true
            }
        }
    }


    private fun onChangePassword() {
        AppUtil.hideKeyboard(binding.llMain)
        if (!GlobalConf.checkInternetConnection(this)) {
            Snackbar.make(binding.llMain,
                    resources.getString(R.string.internet_not_found_str),
                    Snackbar.LENGTH_LONG).show()
            return
        }
        if (!validate) return
        showDialog()

        retroApi.changePassword(hashMapOf(
                "customer_id" to sessionManager.currentUser.userId,
                "current_password" to binding.edtCurrentPwd.text.toString().trim(),
                "new_password" to binding.edtNewPwd.text.toString().trim()
        )).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                handleSuccess(response)
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                dismissDialog()
                DialogUtils.showInfoAlert(this@ChangePasswordActivity,
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
        Toast.makeText(this, genericResponse.message, Toast.LENGTH_LONG).show()

        if (genericResponse.statusInLower == Constant.SUCCESS) {
            finish()
        }
    }

    private val validate
        get() = when {
            binding.edtCurrentPwd.text.toString().trim().isEmpty() -> {
                binding.edtCurrentPwd.error = getString(R.string.current_password)
                binding.edtCurrentPwd.requestFocus()
                false
            }
            binding.edtNewPwd.text.toString().trim().isEmpty() -> {
                binding.edtNewPwd.error = getString(R.string.new_password)
                binding.edtNewPwd.requestFocus()
                false
            }
            binding.edtReNewPwd.text.toString().trim().isEmpty() -> {
                binding.edtReNewPwd.error = getString(R.string.re_new_password)
                binding.edtReNewPwd.requestFocus()
                false
            }
            !AppUtil.isValidPwd(binding.edtNewPwd.text.toString().trim()) -> {
                binding.edtNewPwd.error = getString(R.string.password_validation)
                binding.edtNewPwd.requestFocus()
                false
            }
            binding.edtNewPwd.text.toString().trim() != binding.edtReNewPwd.text.toString().trim() -> {
                binding.edtReNewPwd.error = getString(R.string.password_not_match)
                binding.edtReNewPwd.requestFocus()
                false
            }
            else -> true
        }

    private fun showDialog() {
        dialogUtils.show(this)
    }

    fun dismissDialog() {
        dialogUtils.hide()
    }
}
