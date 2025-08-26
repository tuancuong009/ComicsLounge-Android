package com.comics.lounge.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.databinding.ActivityPasswordResetBinding
import com.comics.lounge.modals.GenericResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.utils.AppUtil
import com.comics.lounge.utils.DialogUtils
import com.comics.lounge.utils.ToolbarUtils.loanAppLogo
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AbstractBaseActivity() {

    lateinit var retroApi: RetroApi
    lateinit var userId: String
    lateinit var binding: ActivityPasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retroApi = ComicsLoungeApp.getRetroApi()

        binding.tb.toolbarAppNameTxt.setText(R.string.reset_pwd)
        binding.tb.toolbar.loanAppLogo(binding.tb.toolbarAppLogo)
        binding.tb.menuRight.visibility = View.INVISIBLE

        userId = intent.getStringExtra(EXTRA_USER_ID)!!

        binding.btnSubmit.setOnClickListener { onResetPassword() }
    }

    private fun onResetPassword() {
        AppUtil.hideKeyboard(binding.llMain)
        if (!GlobalConf.checkInternetConnection(this)) {
            Snackbar.make(binding.llMain,
                    resources.getString(R.string.internet_not_found_str),
                    Snackbar.LENGTH_LONG).show()
            return
        }
        if (!validate) return
        showDialog()

        retroApi.resetPassword(hashMapOf(
                "customer_id" to userId,
                "new_password" to binding.edtNewPwd.text.toString().trim()
        )).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                handleSuccess(response)
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                dismissDialog()
                DialogUtils.showInfoAlert(this@ResetPasswordActivity,
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
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }
    }

    private val validate
        get() = when {
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
        ComicsLoungeApp.dialogUtils.show(this)
    }

    fun dismissDialog() {
        ComicsLoungeApp.dialogUtils.hide()
    }

    companion object {
        const val EXTRA_USER_ID = "user_id"
    }
}
