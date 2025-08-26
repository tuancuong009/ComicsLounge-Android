package com.comics.lounge.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.comics.lounge.ComicsLoungeApp
import com.comics.lounge.activity.AbstractBaseActivity
import com.comics.lounge.activity.MainActivity
import com.comics.lounge.adapter.ShareMembershipAdapter
import com.comics.lounge.conf.Constant
import com.comics.lounge.databinding.MembershipListLayoutBinding
import com.comics.lounge.modals.memberrshipmeta.MembershipMeta
import com.comics.lounge.retrofit.RetroApi
import com.comics.lounge.sessionmanager.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class GiftedMembershipFragment : Fragment() {

    private lateinit var parentActivity: AbstractBaseActivity
    private lateinit var shareMembershipAdapter: ShareMembershipAdapter
    private lateinit var retroApi: RetroApi
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: MembershipListLayoutBinding

    companion object {
        fun newInstant() = GiftedMembershipFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        parentActivity = activity as AbstractBaseActivity
        binding = MembershipListLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(parentActivity)
        retroApi = ComicsLoungeApp.getRetroApi()
        binding.vfMembershipList.displayedChild = 2
        fetchShareMembershipList();
    }

    private fun fetchShareMembershipList() {
        retroApi.run {
            myMembership(sessionManager.currentlyLoggedUserId).enqueue(object : Callback<MembershipMeta> {
            override fun onResponse(call: Call<MembershipMeta>, response: Response<MembershipMeta>) {
                binding.vfMembershipList.displayedChild = 0
                val memberShipResponse = response.body()!!
                if (memberShipResponse.status.lowercase(Locale.ROOT) == Constant.SUCCESS) {
                    if (memberShipResponse.params.isNotEmpty()) {
                        binding.vfMembershipList.displayedChild = 0
                        shareMembershipAdapter = ShareMembershipAdapter(activity!!, memberShipResponse.params, 1,::onItemClick)

                       // shareMembershipAdapter.addClickEvent(::onItemClick)
                        binding.rvShareMembership.adapter = shareMembershipAdapter
                    } else {
                        binding.vfMembershipList.displayedChild = 1
                    }
                }else{
                    binding.vfMembershipList.displayedChild = 1
                }
            }

            override fun onFailure(call: Call<MembershipMeta>, t: Throwable) {
                binding.vfMembershipList.displayedChild = 1
                t.printStackTrace()
            }
        })
        }

    }

    private fun onItemClick(position: Int) {
        //val menuItem = productMetaObj.params.products[position]
        /*startActivity(Intent(activity!!, GiftMembershipActive::class.java).apply {
           // putExtra(PARAM_PRODUCT_OBJ, menuItem.entity_id)
        })*/
    }

}