package com.comics.lounge.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.BuyMembership;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmMyAccountBinding;
import com.comics.lounge.modals.Date;
import com.comics.lounge.modals.user.User;
import com.comics.lounge.modals.user.UserResponse;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DatesUtils;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmMyAccount extends Fragment {
    FrmMyAccountBinding binding;
    SessionManager sessionManager;
    NewMain activity;
    public FrmMyAccount() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmMyAccountBinding.inflate(getLayoutInflater());
        init();

        binding.btReferFr.setOnClickListener(v -> activity.popupInviteFr());
        binding.btChangePw.setOnClickListener(v -> activity.addFrmDetail(new FrmChangePw()));
        binding.edtPw.setOnClickListener(v -> activity.addFrmDetail(new FrmChangePw()));
        binding.rlUpdateMb.setOnClickListener(v -> activity.addFrmDetail(new FrmMbDetail(this)));
//        binding.llMbInfo.setOnClickListener(v -> startActivity(new Intent(activity, BuyMembership.class)));

        return binding.getRoot();
    }

    // init UI
    public void init(){
        sessionManager = new SessionManager(activity);
        loadPw();
        User currentUser = sessionManager.getCurrentUser();
        String membershipName = currentUser.getMembershipname();
        if (membershipName == null || membershipName.isEmpty()) {
            binding.memberShipLayout.setVisibility(View.GONE);
        } else {
            binding.membershipName.setText(Html.fromHtml("Your Membership is currently <font color=#EC027D>ACTIVE</font>", Html.FROM_HTML_MODE_COMPACT));
            binding.memberShipLayout.setVisibility(View.VISIBLE);
        }
        if (currentUser.getExpireInDays() != null){
            String expireInDays = currentUser.getExpireInDays().toString();
            binding.membershipExpireDays.setText(Html.fromHtml("There are <font color=#EC027D>" + expireInDays + " days</font> remaining\non your membership", Html.FROM_HTML_MODE_COMPACT));
            binding.membershipExpireDays.setVisibility(View.VISIBLE);
        }
        binding.tvEmail.setText(currentUser.getEmail());
        binding.tvPhone.setText(currentUser.getMobile());
        binding.edtName.setText(currentUser.getName());
    }
    public void loadPw(){
        binding.edtPw.setText(AppUtil.getPw(activity));
    }
}