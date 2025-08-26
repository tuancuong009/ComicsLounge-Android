package com.comics.lounge.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.comics.lounge.R
import com.comics.lounge.databinding.ActivityShareMembershipBinding
import com.comics.lounge.fragments.GiftedMembershipFragment
import com.comics.lounge.fragments.ShareMembershipFragment
import com.comics.lounge.utils.ToolbarUtils.showBackArrow
import com.google.android.material.tabs.TabLayoutMediator


class ShareMembershipActivity : AbstractBaseActivity() {
    private lateinit var viewPagerFragmentAdapter: ViewPagerFragmentAdapter
    lateinit var binding: ActivityShareMembershipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareMembershipBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.tb.toolbarAppNameTxt.setText(R.string.share_membership_str)
//        binding.tb.toolbar.showBackArrow(this)
        viewPagerFragmentAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        binding.vp2Share.adapter = viewPagerFragmentAdapter
        binding.vp2Share.offscreenPageLimit = viewPagerFragmentAdapter.itemCount
        TabLayoutMediator(binding.tbShareHeading, binding.vp2Share, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.my_membership_str)
                1 -> tab.text = resources.getText(R.string.shared_membership_str)
            }
        }).attach()
        binding.btBack.setOnClickListener{
            finish()
        }

        /*sessionManager = SessionManager(this)
        retroApi = ComicsLoungeApp.getRetroApi()

        toolbar_app_name_txt.setText(R.string.share_membership_str)
        toolbar.showBackArrow(this)
        vsShareMembershipLayout.displayedChild = 1
        fetchShareMembershipList();*/

    }

    /*private fun fetchShareMembershipList() {
        retroApi.fetchMemberShipList(sessionManager.currentlyLoggedUserId).enqueue(object : Callback<MembershipMeta> {
            override fun onResponse(call: Call<MembershipMeta>, response: Response<MembershipMeta>) {
                vsShareMembershipLayout.displayedChild = 0
                val memberShipResponse = response.body()!!
                if (memberShipResponse.status.toLowerCase() == Constant.SUCCESS) {
                    if (memberShipResponse.params.isNotEmpty()) {
                        rvShareMembership.visibility = View.VISIBLE
                        llNoDataFound.visibility = View.GONE
                        shareMembershipAdapter = ShareMembershipAdapter(applicationContext,memberShipResponse.params)
                        rvShareMembership.adapter = shareMembershipAdapter
                    } else {
                        rvShareMembership.visibility = View.GONE
                        llNoDataFound.visibility = View.VISIBLE
                    }
                }else{
                    rvShareMembership.visibility = View.GONE
                    llNoDataFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<MembershipMeta>, t: Throwable) {
                vsShareMembershipLayout.displayedChild = 0
                t.printStackTrace()
                GlobalConf.showServerError(llMainLayout)
            }
        })
    }*/

    class ViewPagerFragmentAdapter(fm: FragmentManager?, lifecycle: Lifecycle) : FragmentStateAdapter(fm!!, lifecycle) {

        private val totalItems = 2

        override fun createFragment(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = GiftedMembershipFragment.newInstant()
                1 -> fragment = ShareMembershipFragment.newInstant()
            }
            return fragment!!
        }

        override fun getItemCount(): Int {
            return totalItems
        }
    }
}