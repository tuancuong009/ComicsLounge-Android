package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.BuyMembership;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.MbPlanAdapter;
import com.comics.lounge.databinding.FrmBecomeMbBinding;
import com.comics.lounge.modals.Membership;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmBecomeMb extends Fragment {
    FrmBecomeMbBinding binding;
    List<Membership> list;
    MbPlanAdapter adapter;
    NewMain activity;
    public FrmBecomeMb() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmBecomeMbBinding.inflate(getLayoutInflater());
        init();

        binding.btCode.setOnClickListener(v -> activity.addFrmDetail(new ClaimMemberShipFragment()));

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        adapter = new MbPlanAdapter(list, activity);
        binding.rcv.setAdapter(adapter);
        binding.rcv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        AppUtil.rcvNoAnimator(binding.rcv);
        getMbShip();
    }

    // get membership description
    private void getMbShip(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getMbShipDes().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                list.clear();
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray jsArr = jsonObject.optJSONArray("memberships");
                            if (jsArr != null && jsArr.length() > 0){
                                for (int i = 0;i < jsArr.length();i++){
                                    JSONObject jsMb = (JSONObject) jsArr.get(i);
                                    Membership membership = new Membership(Integer.parseInt(jsMb.optString("membership_id")), jsMb.optString("membershipname"), jsMb.optString("description"),
                                            jsMb.optString("price"), jsMb.optString("other_price"), jsMb.optString("image"), jsMb.optString("create_on"),
                                            jsMb.optString("status"), jsMb.optString("permonthallowed"));
                                    list.add(membership);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                binding.pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppUtil.hideLoading();
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}