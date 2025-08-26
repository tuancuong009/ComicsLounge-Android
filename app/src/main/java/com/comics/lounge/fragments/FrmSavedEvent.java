package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.EventAdapter;
import com.comics.lounge.databinding.FrmSavedEventBinding;
import com.comics.lounge.modals.EventNew;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmSavedEvent extends Fragment {
    FrmSavedEventBinding binding;
    NewMain activity;
    List<EventNew> list;
    EventAdapter adapter;
    SessionManager sessionManager;
    public FrmSavedEvent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmSavedEventBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        sessionManager = new SessionManager(activity);
        list = new ArrayList<>();
        adapter = new EventAdapter(activity, list);
        binding.rcvSaved.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        AppUtil.rcvNoAnimator(binding.rcvSaved);
        binding.rcvSaved.setAdapter(adapter);
        AppUtil.showLoading(activity);
        getEvent();
    }

    // get event
    private void getEvent(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getSavedEvent(sessionManager.getCurrentlyLoggedUserId()).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                JSONArray jsonArray;
                if (response.isSuccessful()){
                    try {
                        jsonArray = new JSONArray(String.valueOf(response.body()));
                        if (jsonArray.length() > 0){
                            for (int i = 0;i < jsonArray.length();i++){
                                JSONObject objPrd = (JSONObject) jsonArray.get(i);
                                list.add(new EventNew(objPrd.optString("product_id"), objPrd.optString("product_name"),
                                        objPrd.optString("img"), objPrd.optString("start_date"), objPrd.optString("end_date"),
                                        true, objPrd.optBoolean("memberAccess")));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                binding.tvNoData.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                AppUtil.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                AppUtil.hideLoading();
                binding.tvNoData.setVisibility(View.VISIBLE);
            }
        });
    }
}