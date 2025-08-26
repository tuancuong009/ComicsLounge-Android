package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.pager.GalleryPagerAdapter;
import com.comics.lounge.adapter.pager.TabMenuAdapter;
import com.comics.lounge.databinding.FrmMenuBinding;
import com.comics.lounge.modals.Categories;
import com.comics.lounge.retrofit.RetroApi;
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


public class FrmMenu extends Fragment {
    FrmMenuBinding binding;
    NewMain activity;
    List<Categories> categoriesList;
    TabMenuAdapter tabMenuAdapter;
    public FrmMenu() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmMenuBinding.inflate(getLayoutInflater());
        init();

        binding.tabMenu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vpMenu.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.vpMenu.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabMenu.selectTab(binding.tabMenu.getTabAt(position));
            }
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        categoriesList = new ArrayList<>();
        getCategories();
    }

    // get menu categories
    private void getCategories(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getCategories("menu/categories.php").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray arrData = jsonObject.optJSONArray("data");
                            if (arrData != null){
                                String cateName;
                                for (int i = 0;i < arrData.length();i++){
                                    JSONObject objData = (JSONObject) arrData.get(i);
                                    cateName = objData.optString("name");
                                    categoriesList.add(new Categories(objData.optString("id"), cateName));
                                    binding.tabMenu.addTab(binding.tabMenu.newTab().setText(cateName));
                                }
                            }
                        }else {
                            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.pbLoading.setVisibility(View.GONE);
                tabMenuAdapter = new TabMenuAdapter(getChildFragmentManager(), getLifecycle(), categoriesList);
                binding.vpMenu.setAdapter(tabMenuAdapter);
//                binding.vpMenu.setOffscreenPageLimit(categoriesList.size());
                binding.divider.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}