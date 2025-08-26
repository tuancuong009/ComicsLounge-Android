package com.comics.lounge.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.DropInClient
import com.braintreepayments.api.DropInListener
import com.braintreepayments.api.DropInRequest
import com.braintreepayments.api.DropInResult
import com.braintreepayments.api.PayPalAccountNonce
import com.braintreepayments.api.PayPalCheckoutRequest
import com.braintreepayments.api.PayPalClient
import com.braintreepayments.api.PayPalListener
import com.comics.lounge.conf.Constant.EXTRA_MESSAGE
import com.comics.lounge.conf.Constant.EXTRA_PAYPAL_TOKEN
import com.comics.lounge.conf.Constant.EXTRA_SUCCESS
import com.comics.lounge.conf.Constant.EXTRA_TOTAL
import com.comics.lounge.conf.Constant.PAYMENT_CURRENCY_CODE
import com.comics.lounge.conf.Constant.REQUEST_PAY_WITH_PAYPAL
import com.comics.lounge.databinding.ActivityPaypalBinding
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PaypalActivity : AppCompatActivity(), DropInListener, PayPalListener {

    private var braintreeClient: BraintreeClient? = null
    private lateinit var payPalClient: PayPalClient

    // Let's wait for paypal all responses
    private val RESPONCE_DELAY = 7L

    private val source = BehaviorSubject.create<Pair<Boolean, String?>>()
    private val compositeDisposable = CompositeDisposable()
    lateinit var binding: ActivityPaypalBinding
    private lateinit var dropInClient: DropInClient
    private lateinit var dropInRequest: DropInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaypalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        compositeDisposable.add(
            source
                .toFlowable(BackpressureStrategy.LATEST)
                .debounce(RESPONCE_DELAY, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.i("Success: $it")
                    setResult(it.first, it.second)
                }, {
                    it.printStackTrace()
                })
        )

        intent.getStringExtra(EXTRA_PAYPAL_TOKEN)?.let { token ->
//            braintreeClient = BraintreeClient(this, token)
//            payPalClient = PayPalClient(braintreeClient!!)
//            payPalClient.setListener(this)
            dropInClient = DropInClient(this, token)

//            // Make sure to register listener in onCreate
            dropInClient.setListener(this)
            dropInRequest = DropInRequest()
//            launchDropIn()

        } ?: run {
            Timber.e("Payment cancelled")
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_LONG).show()
            source.onNext(false to null)
        }

    }

    override fun onResume() {
        super.onResume()
//        initPayPalDropRequest()
        launchDropIn()
    }


    private fun launchDropIn() {

        dropInClient.launchDropIn(dropInRequest)
    }

    private fun initPayPalDropRequest() {
        val total = intent.getDoubleExtra(EXTRA_TOTAL, 0.0)
        val request = PayPalCheckoutRequest(total.toString())
        request.currencyCode = PAYMENT_CURRENCY_CODE
//        request.intent = PayPalPaymentIntent.AUTHORIZE

        payPalClient.tokenizePayPalAccount(this, request)
        braintreeClient?.let {
            val result = braintreeClient!!.deliverBrowserSwitchResult(this)
            result?.let { browserSwitchResult ->
                payPalClient.onBrowserSwitchResult(browserSwitchResult) { payPalAccountNonce, error ->
                    /*if (payPalAccountNonce != null) setResult(true, payPalAccountNonce.string)
                    if (error != null) {
                        setResult(false, error.message)
                    }*/

                    if (payPalAccountNonce != null) {
                        source.onNext(true to payPalAccountNonce.string)
                    }
                    if (error != null) {
                        source.onNext(false to error.message)
                    }
                }
            }
        }
    }

    private fun setResult(success: Boolean, message: String?) {
        setResult(
            REQUEST_PAY_WITH_PAYPAL,
            Intent().apply {
                putExtra(EXTRA_SUCCESS, success)
                putExtra(EXTRA_MESSAGE, message)
            }
        )
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onPayPalSuccess(payPalAccountNonce: PayPalAccountNonce) {
        setResult(true, payPalAccountNonce.string)
    }

    override fun onPayPalFailure(error: Exception) {
    }

    override fun onDropInSuccess(dropInResult: DropInResult) {
        val nonce = dropInResult.paymentMethodNonce!!.string
        setResult(true, nonce)
    }

    override fun onDropInFailure(error: Exception) {
        setResult(false, "Payment cancelled")
        finish()
    }
}