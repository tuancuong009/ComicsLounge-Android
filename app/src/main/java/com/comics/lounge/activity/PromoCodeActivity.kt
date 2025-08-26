package com.comics.lounge.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.Constant.LOCALHOST
import com.comics.lounge.conf.Constant.REFRESH_AFTER_PAY
import com.comics.lounge.conf.Constant.REQUEST_PAY_WITH_PAYPAL
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.databinding.ActivityPromoCodeBinding
import com.comics.lounge.modals.CouponResponse
import com.comics.lounge.modals.DiscountType
import com.comics.lounge.modals.Membership
import com.comics.lounge.modals.TokenResponse
import com.comics.lounge.modals.memberrshipmeta.MembershipMeta
import com.comics.lounge.modals.user.UserResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.utils.AppUtil
import com.comics.lounge.utils.DialogUtils.Companion.showInfoAlert
import com.comics.lounge.utils.NumberUtils
import com.comics.lounge.utils.ToolbarUtils.showBackArrow
import com.comics.lounge.utils.UserUtils.fetchAndUpdateUser
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PromoCodeActivity : AbstractBaseActivity() {

    lateinit var sessionManager: SessionManager
    lateinit var retroApi: RetroApi
    lateinit var membership: Membership
    lateinit var binding: ActivityPromoCodeBinding
    var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromoCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        retroApi = ComicsLoungeApp.getRetroApi()

        binding.btBack.setOnClickListener {
            finish()
        }
        membership = (intent!!.getSerializableExtra("membership") as Membership?)!!
        loadMembershipDetails()

        binding.btnSubmit.setOnClickListener {
            if (binding.isCheckbox.isChecked) {
                if (!AppUtil.isValidEmail(binding.edtEmail.text.toString())) {
                    binding.edtEmail.error = resources.getString(R.string.enter_valid_email)
                    binding.edtEmail.requestFocus()
                } else if (binding.edtMobile.text.toString().isEmpty()) {
                    binding.edtMobile.error = resources.getString(R.string.mobile_nom_not_blank)
                    binding.edtMobile.requestFocus()
                } else if (binding.edtMobile.text!!.length < 6) {
                    binding.edtMobile.error = resources.getString(R.string.mobile_number_length_error)
                    binding.edtMobile.requestFocus()
                } else {
                    // purchaseMembership("DEMO")
                    processToPay()
                }
            } else {
                Log.d("membership_id :- ", "" + sessionManager.currentUser)
//                Log.d("membership_id :- ", membership.id.toString())
//                Log.d("membership_id :- ", sessionManager.currentUser.toString())

                if (sessionManager.currentUser.memershipId != null && sessionManager.currentUser.memershipId!!.toInt() == membership.id && sessionManager.currentUser.membership!!
                    && sessionManager.currentUser.expireInDays?.toInt()!! >= 29
                ) {
                    showInfoAlert(
                        this,
                        resources.getString(R.string.error_str),
                        resources.getString(R.string.gift_membership_error_str)
                    )
                    /*if (sessionManager.currentUser.expireStatus.equals("0")){
                        processToPay()
                        //showInfoAlert(this, resources.getString(R.string.error_str), resources.getString(R.string.membership_exired))
                    }else{
                        showInfoAlert(this, resources.getString(R.string.error_str), resources.getString(R.string.gift_membership_error_str))
                    }*/
                } else {
                    Log.d("nOT CALLL", "not")
                    processToPay()
                }
                /*if (sessionManager.currentUser.membership == true) {
                    showInfoAlert(this, resources.getString(R.string.error_str), resources.getString(R.string.gift_membership_error_str))
                } else processToPay()*/
            }
        }


        binding.ivMemberInfo.setOnClickListener {
            showInfoAlert(this, membership.name, membership.description)
        }

        binding.btnApply.setOnClickListener {
            applyPromoCode()
        }


        binding.ivGiftMembership.setOnClickListener {
            showInfoAlert(this, "", resources.getString(R.string.gif_membership_dtails))
        }


        binding.isCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.llShareMembershiptForm.visibility = View.VISIBLE
            } else {
                binding.llShareMembershiptForm.visibility = View.GONE
            }
        }

        /*btnShare.setOnClickListener {
            if (!AppUtil.isValidEmail(binding.edtEmail.text.toString())){
                edtEmail.error = resources.getString(R.string.enter_valid_email)
            }else if (edtMobile.text.toString().isEmpty()){
                edtMobile.error = resources.getString(R.string.mobile_nom_not_blank)
            }else if(edtMobile.text!!.length<6){
                edtMobile.error = resources.getString(R.string.mobile_number_length_error)
            }else{

            }
        }*/
    }

    private fun applyPromoCode() {
        AppUtil.hideKeyboard(binding.btnApply)

        if (binding.edtPromoCode.text.toString().trim().isEmpty()) {
            binding.edtPromoCode.error = "Enter Promo code"
            binding.edtPromoCode.requestFocus()
            return
        }
        showApplyLoader(true)

        retroApi.validateCoupon(
            hashMapOf(
                "user_id" to sessionManager.currentUser.userId,
                "membership_id" to membership.id,
                "Coupon_code" to binding.edtPromoCode.text.toString().trim()
            )
        ).enqueue(object : Callback<CouponResponse> {

            override fun onResponse(
                call: Call<CouponResponse>,
                response: Response<CouponResponse>
            ) {
                showApplyLoader(false)

                if (!response.isSuccessful) {
                    GlobalConf.showServerError(binding.btnSubmit); return
                }

                val couponResponse = response.body()!!
                if (couponResponse.statusInLower == Constant.SUCCESS) {
                    calculateTotal(couponResponse.param)
                }

                couponResponse.param?.message?.let {
                    binding.tvPromoMessage.text = it
                    binding.tvPromoMessage.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<CouponResponse>, t: Throwable) {
                showApplyLoader(false)
                t.printStackTrace()
                GlobalConf.showServerError(binding.btnSubmit)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_PAY_WITH_PAYPAL) {
            val success = data?.getBooleanExtra(Constant.EXTRA_SUCCESS, false)
            val message = data?.getStringExtra(Constant.EXTRA_MESSAGE)

            Timber.i("Payment: success: $success message:$message")

            if (success == true) {
                val successStr = getString(R.string.paypal_success_value)
                callPayPalLog(successStr)
                purchaseMembership(message!!)
            }
            if (success == false) {
                binding.viewSwitcher.showPrevious()
                callPayPalLog(message!!)
                showDialogForpaymentFail(message)
             //  Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun showDialogForpaymentFail(message: String) {
        val mBuilder = AlertDialog.Builder(this)
            .setTitle("Error!!")
//            .setMessage(
//                getString(R.string.payment_error1) + " " + getString(R.string.country_code_val_str) + edtMobile.text.toString() + " " + getString(
//                    R.string.payment_error2)
        .setMessage(
              message
            )
            .setPositiveButton("ok", null)


        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()

        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            // Programmatically closing the AlertDialog
            mAlertDialog.cancel()
        }
    }
    private fun showApplyLoader(show: Boolean) {
        binding.tvPromoMessage.visibility = View.GONE
        if (show) {
            binding.rlProgress.visibility = View.VISIBLE
            binding.btnApply.visibility = View.INVISIBLE
            binding.edtPromoCode.isEnabled = false
        } else {
            binding.rlProgress.visibility = View.INVISIBLE
            binding.btnApply.visibility = View.VISIBLE
            binding.edtPromoCode.isEnabled = true
        }
    }

    private fun loadMembershipDetails() {
        binding.tvMemberTitle.text = membership.name
        binding.tvMemberShipFee.text = NumberUtils.formatMoney(membership.price)
        calculateTotal()
    }

    private fun processToPay() {

        if (!GlobalConf.checkInternetConnection(this, binding.btnSubmit)) return
        binding.viewSwitcher.showNext()    //show process
        retroApi.generateToken().enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (!response.isSuccessful || response.body()?.token.isNullOrEmpty()) {
                    binding.viewSwitcher.showPrevious()    //hide process
                    GlobalConf.showServerError(binding.btnSubmit); return
                }
                initPayPalDropRequest(response.body()!!.token!!)
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                t.printStackTrace()
                GlobalConf.showServerError(binding.btnSubmit)
            }
        })
    }

    private fun initPayPalDropRequest(payPalToken: String) {
        startActivityForResult(
            Intent(this, PaypalActivity::class.java).apply {
                putExtra(Constant.EXTRA_TITLE, getString(R.string.membership_buy))
                putExtra(Constant.EXTRA_PAYPAL_TOKEN, payPalToken)
                putExtra(Constant.EXTRA_TOTAL, total)
            }, REQUEST_PAY_WITH_PAYPAL
        )
    }

    private fun purchaseMembership(paymentNone: String) {
        var giftVal = "0"
        if (binding.isCheckbox.isChecked) {
            giftVal = "1"
        }
        /*var hasmapv = hashMapOf(
                "user_id" to sessionManager.currentUser.userId,
                "membership_id" to membership.id,
                "paymentMethodNonce" to paymentNone,
                "amount" to total.toString(),
                "gift" to giftVal,
                "phone" to edtMobile.text.toString(),
                "email" to edtEmail.text.toString(),
                "user_ip_address" to LOCALHOST
        )
        Log.d("HAsh_MAP :: ",hasmapv.toString())*/
        retroApi.payMembershipFree(
            hashMapOf(
                "user_id" to sessionManager.currentUser.userId,
                "membership_id" to membership.id,
                "paymentMethodNonce" to paymentNone,
                "amount" to total.toString(),
                "gift" to giftVal,
                "phone" to binding.edtMobile.text.toString(),
                "email" to binding.edtEmail.text.toString(),
                "country_code" to Constant.COUNTRY_CODE,
                "user_ip_address" to LOCALHOST,
                "first_name" to binding.edtFirstName.text.toString(),
                "last_name" to binding.edtLastName.text.toString()

            )
        ).enqueue(object : Callback<MembershipMeta> {
            override fun onResponse(call: Call<MembershipMeta>, response: Response<MembershipMeta>) {
                Log.w("KRUTI", "onResponse: "+response.toString() )
                if (!response.isSuccessful) {
                    binding.viewSwitcher.showPrevious()    //hide process
                    GlobalConf.showServerError(binding.btnSubmit); return
                }

                val userResponse = response.body()!!
                if (userResponse.status == Constant.SUCCESS) {
                    updateLocalUser(userResponse.message)
                } else {
                    binding.viewSwitcher.showPrevious()    //hide process
                    Snackbar.make(binding.btnSubmit, userResponse.status!!, Snackbar.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<MembershipMeta>, t: Throwable) {
                t.printStackTrace()
                GlobalConf.showServerError(binding.btnSubmit)
            }
        })
    }

    private fun updateLocalUser(message: String) {
        fetchAndUpdateUser(
            retroApi = retroApi,
            sessionManager = sessionManager,
            userId = sessionManager.currentUser.userId,
            onError = object : (String?) -> Unit {
                override fun invoke(p1: String?) {
                    GlobalConf.showServerError(binding.btnSubmit)
                }
            }
        ) {
            binding.viewSwitcher.showPrevious()    //hide process
            showInfoAlert(this, "Payment", message, cancelable = false) {
                setResult(REFRESH_AFTER_PAY, Intent().apply {
                    putExtra("name", membership.name)
                })
                finish()
            }
        }
    }

    private fun calculateTotal(coupon: CouponResponse.Coupon? = null) {
        total = NumberUtils.parseMoney(membership.price)
        var discount = 0.0f

        coupon?.let {
            if (it.discount_type == DiscountType.Percentage.type) {
                discount = ((total * it.discount) / 100).toFloat()
                total -= discount
            } else {
                discount = it.discount
                total -= discount
            }
        }
        binding.tvDiscountAmount.text = NumberUtils.formatMoney(discount)
        binding.tvTotal.text = NumberUtils.formatMoney(total)
    }

    private fun callPayPalLog(errorMsg: String) {
        retroApi.insertPaymentLog(
            hashMapOf(
                "user_id" to sessionManager.currentUser.userId,
                "log_text" to errorMsg,
                "app_type" to Constant.DEVICE_TYPE,
                "app_version" to AppUtil.getBuildVersionCode(this)
            )
        ).enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
            }
        })
    }

    companion object {
        const val EXTRA_MEMBERSHIP = "membership"
    }
}