package com.comics.lounge.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.FoodAdapter;
import com.comics.lounge.databinding.FrmFoodBinding;
import com.comics.lounge.databinding.PopupMenuOptionBinding;
import com.comics.lounge.modals.Gallery;
import com.comics.lounge.modals.MenuItem;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmFood extends Fragment {
    FrmFoodBinding binding;
    NewMain activity;
    FoodAdapter adapter;
    List<MenuItem> list;
    List<String> listImgUrl;
    SessionManager sessionManager;
    RetroApi api = ComicsLoungeApp.getRetroApi();
    boolean isLoaded = false;
    public FrmFood() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmFoodBinding.inflate(getLayoutInflater());
        init();

        binding.btDownMenu.setOnClickListener(v -> {
            for (String img : listImgUrl){
                AppUtil.downloadImg(activity, "menu"+System.currentTimeMillis(), img);
            }
            Toast.makeText(activity, activity.getString(R.string.downloading), Toast.LENGTH_SHORT).show();
        });
        binding.swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swRefresh.setRefreshing(false);
                binding.pbLoading.setVisibility(View.VISIBLE);
                getMenuItem();
            }
        });
        return binding.getRoot();
    }

    // init UI
    private void init(){
        sessionManager = new SessionManager(activity);
        list = new ArrayList<>();
        listImgUrl = new ArrayList<>();
        adapter = new FoodAdapter(activity, list);
        binding.rcvFood.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rcvFood.setAdapter(adapter);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && !isLoaded){
            getMenuItem();
            isLoaded = true;
        }
    }

    // get menu item
    private void getMenuItem(){
        api.getItem("menu/items.php", Integer.parseInt(getArguments().getString("id")), Integer.parseInt(sessionManager.getCurrentlyLoggedUserId())).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                list.clear();
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray arrData = jsonObject.optJSONArray("data");
                            if (arrData != null){
                                for (int i = 0;i < arrData.length();i++){
                                    JSONObject objData = (JSONObject) arrData.get(i);
                                    list.add(new MenuItem(objData.optString("name"), objData.optString("price"),
                                            objData.optString("description"), objData.optString("medium_size"), objData.optString("popup_image")));
                                    listImgUrl.add(objData.optString("popup_image"));
                                }
                            }
                        }else {
                            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.rcvFood.setVisibility(View.VISIBLE);
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                binding.btDownMenu.setVisibility(list.size() == 0 ? View.GONE : View.VISIBLE);
                adapter.notifyDataSetChanged();
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