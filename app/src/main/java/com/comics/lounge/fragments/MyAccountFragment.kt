package com.comics.lounge.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.R
import com.comics.lounge.activity.ChangePasswordActivity
import com.comics.lounge.activity.NewMain
import com.comics.lounge.activity.ShareMembershipActivity
import com.comics.lounge.databinding.MyAccountFragmentLayoutBinding
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import com.comics.lounge.utils.DatesUtils
import com.comics.lounge.utils.UserUtils


class MyAccountFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var retroApi: RetroApi
    private var parentActivity: NewMain? = null
    lateinit var binding: MyAccountFragmentLayoutBinding

    var total = 0.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = activity as NewMain
        binding = MyAccountFragmentLayoutBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retroApi = ComicsLoungeApp.getRetroApi()
        sessionManager = SessionManager(requireActivity())
        fetchUserDetails(view)
//        binding.memberShipLayout.setOnClickListener {
//            startActivity(Intent(parentActivity, BuyMembership::class.java))
//        }

        binding.ivEdit.setOnClickListener {
            startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
        }
        binding.renewMembership.setOnClickListener {
            parentActivity?.addFrmDetail(FrmBecomeMb())
        }
        binding.btMbVideo.setOnClickListener { parentActivity!!.popupVideo() }
        updateUserDetail()

        binding.llMbList.setOnClickListener {
            switchToMemberShipListActivity()
        }
        binding.redeemMembership.setOnClickListener {
            parentActivity?.addFrmDetail(ClaimMemberShipFragment())
        }

        if (sessionManager.freeEventRestoredCount == "0") {
            binding.txtFreeEvent.text =
                requireActivity().resources.getString(R.string.event_count_allowed) + " : " + sessionManager.currentUser.eventCountAllowed + "\n" + requireActivity().resources.getString(
                    R.string.event_count_left
                ) + " : " + sessionManager.currentUser.eventCountAllowed
        }else{
            binding.txtFreeEvent.text =
                requireActivity().resources.getString(R.string.event_count_allowed) + " : " + sessionManager.currentUser.eventCountAllowed + "\n" + requireActivity().resources.getString(
                    R.string.event_count_left
                ) + " : " + sessionManager.currentUser.eventCountAllowed +"\n"+requireActivity().resources.getString(R.string.free_event_restored) + " : " + sessionManager.currentUser.freeEventRestored
        }

        if (sessionManager.currentUser.memershipId != null  && sessionManager.currentUser.membership!!
            && sessionManager.currentUser.expireInDays?.toInt()!! <= 28
        ) {
            binding.renewMembership.visibility = View.VISIBLE
            binding.redeemMembership.visibility = View.VISIBLE
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(resources.getString(R.string.buy_membership))
            builder.setCancelable(false)
            builder.setMessage(resources.getString(R.string.membership_exired_in)+" "+sessionManager.currentUser.expireInDays+" Days\n"+
                    resources.getString(R.string.you_buy_membership))
            builder.setPositiveButton("ok"){
                    dialog, which -> dialog.cancel()
                parentActivity?.addFrmDetail(FrmBecomeMb())
            }
            builder.setNegativeButton("Dismiss"){
                    dialog, which -> dialog.cancel()
            }

            builder.show()

        }else{
            binding.renewMembership.visibility = View.GONE
            binding.redeemMembership.visibility = View.GONE
        }

    }


    private fun switchToMemberShipListActivity() {
        startActivity(Intent(requireActivity(), ShareMembershipActivity::class.java))
    }

    private fun fetchUserDetails(view: View) {
        UserUtils.fetchAndUpdateUser(
            retroApi = retroApi,
            sessionManager = sessionManager,
            userId = sessionManager.currentUser.userId,
            onError = object : (String?) -> Unit {
                override fun invoke(p1: String?) {
//                    GlobalConf.showServerError(view)
                }
            }
        ) {
            updateUserDetail()
        }
    }

    private fun updateUserDetail() {
        with(sessionManager.currentUser) {
            binding.userNameTxt.text = name
            binding.nameTxt.text = name
            binding.mobileNoTxt.text = mobile
            binding.membershipName.text =
                if (membershipname.isNullOrEmpty()) getString(R.string.no_membership_purchased) else membershipname
            startDate?.let {
                binding.membershipExpireDate.text =
                    """${DatesUtils.AppDateFormat(it, "yyyy-MM-dd")}"""
                binding.membershipExpireDate.visibility = View.VISIBLE
            }
            expireInDays?.let {
                binding.membershipExpireDays.text = "$it days"
                binding.membershipExpireDays.visibility = View.VISIBLE
            }

            when (noOfStrike) {
                0 -> {
                    binding.tvStrikes.text = "0 strikes"
                }
                1 -> {
                    binding.tvStrikes.text = "1 strike"
                }
                2 -> {
                    binding.tvStrikes.text = "2 strikes"
                }
                else -> {
                    binding.tvStrikes.text = "3 strikes(your membership is currently suspended)\n" +
                            "renewal date is " + "(" + activationDate + ")"
                }
            }
        }
        binding.ticketCounter.text =
            if (sessionManager.totalFreeTickets == 0) getString(R.string.no_free_tickets)
            else "Available " + sessionManager.totalFreeTickets + " Free Pass."
    }
}