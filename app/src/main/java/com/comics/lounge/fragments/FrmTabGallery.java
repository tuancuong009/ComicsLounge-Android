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
import com.comics.lounge.databinding.FrmTabGalleryBinding;
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


public class FrmTabGallery extends Fragment {
    FrmTabGalleryBinding binding;
    List<Categories> categoriesList;
    GalleryPagerAdapter galleryPagerAdapter;
    NewMain activity;
    String type;
    public FrmTabGallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmTabGalleryBinding.inflate(getLayoutInflater());
        init();

        binding.vpGallery.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabGallery.selectTab(binding.tabGallery.getTabAt(position));
            }
        });
        binding.tabGallery.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.vpGallery.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return binding.getRoot();
    }

    // init UI
    private void init(){
        categoriesList = new ArrayList<>();
        type = getArguments().getString("type");
        if (type.equals("photo")) {
            getGalleryCate("gallery/categories.php");
        }else {
            getGalleryCate("gallery/videocategories.php");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.getRoot().requestLayout();
    }

    // get gallery category
    private void getGalleryCate(String url){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getCategories(url).enqueue(new Callback<JsonObject>() {
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
                                    binding.tabGallery.addTab(binding.tabGallery.newTab().setText(cateName));
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
                galleryPagerAdapter = new GalleryPagerAdapter(getChildFragmentManager(), getLifecycle(), categoriesList, type);
                binding.vpGallery.setAdapter(galleryPagerAdapter);
                binding.vpGallery.setOffscreenPageLimit(categoriesList.size());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}