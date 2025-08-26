package com.comics.lounge.fragments;

import android.app.Dialog;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.SpecialOfferAdapter;
import com.comics.lounge.adapter.pager.OfferPagerAdapter;
import com.comics.lounge.databinding.FrmSpecialOfferBinding;
import com.comics.lounge.databinding.PopupViewOfferBinding;
import com.comics.lounge.modals.Offers;
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


public class FrmSpecialOffer extends Fragment {
    FrmSpecialOfferBinding binding;
    NewMain activity;
    SpecialOfferAdapter adapter;
    List<Offers> list;
    public FrmSpecialOffer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmSpecialOfferBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        list = new ArrayList<>();
        adapter = new SpecialOfferAdapter(activity, list);
        binding.rcvOffer.setAdapter(adapter);
        binding.rcvOffer.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        getItem();
    }

    // get offer item
    private void getItem(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getOfferItem("offers/offers.php", Integer.parseInt(getArguments().getString("id")), 1, 10).enqueue(new Callback<JsonObject>() {
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
                                    list.add(new Offers(obj.optString("id"), obj.optString("description"), obj.optString("image")));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                binding.pbLoading.setVisibility(View.GONE);
                if (adapter.getItemCount() == 0){
                    binding.tvNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}