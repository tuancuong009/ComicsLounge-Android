package com.comics.lounge.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.activity.NewMain
import com.comics.lounge.conf.Constant
import com.comics.lounge.conf.GlobalConf
import com.comics.lounge.databinding.GiftMembershipActiveBinding
import com.comics.lounge.modals.GenericResponse
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.utils.AppUtil
import com.comics.lounge.utils.UserUtils
import com.comics.lounge.utils.Utils.generateClickableLinkToFrm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClaimMemberShipFragment : Fragment() {

    private lateinit var parentActivity: NewMain
    private lateinit var retroApi: RetroApi
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: GiftMembershipActiveBinding

    companion object {
        fun newInstant() = ClaimMemberShipFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = activity as NewMain
        binding = GiftMembershipActiveBinding.inflate(layoutInflater)

        AppUtil.disableBt(binding.btnSubmit)
        binding.edtCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    AppUtil.enableBt(binding.btnSubmit)
                }else{
                    AppUtil.disableBt(binding.btnSubmit)
                }
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(parentActivity)
        retroApi = ComicsLoungeApp.getRetroApi()

        binding.btnSubmit.setOnClickListener {
            callApi();
        }
    }

    private fun callApi() {
        binding.vsMembership.showNext()    //show process
        retroApi.fetchConfirmMembership(
            hashMapOf(
                "user_id" to sessionManager.currentUser.userId,
                "code" to binding.edtCode.text.toString().trim()
            )
        ).enqueue(object : Callback<GenericResponse> {
            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Toast.makeText(activity!!, t.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                binding.vsMembership.showPrevious()
                if (response.body()!!.status == Constant.SUCCESS) {
                    fragmentManager!!.popBackStack();
                    Toast.makeText(activity!!, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    UserUtils.fetchAndUpdateUser(
                        retroApi = retroApi,
                        sessionManager = sessionManager,
                        userId = sessionManager.currentUser.userId,
                        onError = object : (String?) -> Unit {
                            override fun invoke(p1: String?) {
                                GlobalConf.showServerError(binding.btnSubmit)
                            }
                        }
                    )
                    parentActivity.onBackPressedDispatcher.onBackPressed()
                } else {
                    Toast.makeText(activity!!, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}