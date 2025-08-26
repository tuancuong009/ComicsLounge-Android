package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.pager.OfferPagerAdapter;
import com.comics.lounge.databinding.FrmOffersBinding;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.utils.AppUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmOffers extends Fragment {
    FrmOffersBinding binding;
    NewMain activity;
    OfferPagerAdapter adapter;
    List<String> cateIdList;
    public FrmOffers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmOffersBinding.inflate(getLayoutInflater());
        init();

        binding.tabOffer.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vpOffer.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.vpOffer.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabOffer.selectTab(binding.tabOffer.getTabAt(position));
            }
        });
        return binding.getRoot();
    }

    // init UI
    private void init(){
        cateIdList = new ArrayList<>();
        getCate();
    }

    // get offer category
    private void getCate(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getCategories("offers/categories.php").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray arrData = jsonObject.optJSONArray("data");
                            if (arrData != null && arrData.length() > 0){
                                for (int i = 0;i < arrData.length();i++){
                                    JSONObject obj = (JSONObject) arrData.get(i);
                                    cateIdList.add(obj.optString("id"));
                                    binding.tabOffer.addTab(binding.tabOffer.newTab().setText(obj.optString("name")));
                                }

                            }
                        }else {
                            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (cateIdList.isEmpty()){
                    binding.tvNoData.setVisibility(View.VISIBLE);
                }else {
                    adapter = new OfferPagerAdapter(getChildFragmentManager(), getLifecycle(), cateIdList);
                    binding.vpOffer.setAdapter(adapter);
                }
                binding.pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}