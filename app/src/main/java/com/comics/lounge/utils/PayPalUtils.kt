package com.comics.lounge.utils

import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.*
import com.comics.lounge.conf.Constant

class PayPalUtils(private val activity: AppCompatActivity) {

    private var braintreeClient: BraintreeClient? = null
    private lateinit var payPalClient: PayPalClient

    fun init(paypalToken: String) {
        braintreeClient = BraintreeClient(activity, paypalToken)
        payPalClient = PayPalClient(braintreeClient!!)
    }

    fun startPayment(
        total: Double,
        onError: (String) -> Unit
    ) {
        val request = PayPalCheckoutRequest(total.toString())
        request.currencyCode = Constant.PAYMENT_CURRENCY_CODE
        request.intent = PayPalPaymentIntent.AUTHORIZE

        payPalClient.tokenizePayPalAccount(activity, request) { error ->
            if (error != null) onError.invoke(error.message!!)
        }
    }

    fun onResume(
        onError: (String?) -> Unit,
        onSuccess: (String) -> Unit
    ) {
        braintreeClient?.let {
            val result = braintreeClient!!.deliverBrowserSwitchResult(activity)
            result?.let { browserSwitchResult ->
                payPalClient.onBrowserSwitchResult(browserSwitchResult) { payPalAccountNonce, error ->
                    if (payPalAccountNonce != null) onSuccess.invoke(payPalAccountNonce.string)
                    if (error != null) onError.invoke(error.message!!)
                }
            }
        }
    }
}
