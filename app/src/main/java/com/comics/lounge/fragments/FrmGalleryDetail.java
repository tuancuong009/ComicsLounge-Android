package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.GalleryAdapter;
import com.comics.lounge.databinding.FrmGalleryDetailBinding;
import com.comics.lounge.modals.Gallery;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
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

public class FrmGalleryDetail extends Fragment {
    FrmGalleryDetailBinding binding;
    NewMain activity;
    GalleryAdapter galleryAdapter;
    List<Gallery> list;
    boolean isLoaded = false, isLoadMore = false;
    String type;
    RetroApi api = ComicsLoungeApp.getRetroApi();
    SessionManager sessionManager;
    int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmGalleryDetailBinding.inflate(getLayoutInflater());
        init();

        binding.nsc.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) && type.equals("photo")) {
                if (isLoadMore) {
                    page++;
                    isLoadMore = false;
                    binding.pbLoading.setVisibility(View.VISIBLE);
                    getPhotoGallery(true);
                }
            }
        });
        binding.swRefresh.setOnRefreshListener(() -> {
            if (type.equals("photo")) {
                page = 1;
                list.clear();
                getPhotoGallery(false);
            }else {
                getVideoGallery();
            }
            binding.swRefresh.setRefreshing(false);
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        sessionManager = new SessionManager(activity);
        binding.swRefresh.setColorSchemeColors(activity.getColor(R.color.bg_splash));
        binding.rcvGallery.setLayoutManager(new GridLayoutManager(activity, 2));
        AppUtil.rcvNoAnimator(binding.rcvGallery);
        type = getArguments().getString("type");
        list = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(activity, list, false);
        binding.rcvGallery.setAdapter(galleryAdapter);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible && !isLoaded){
            if (type.equals("photo")) {
                getPhotoGallery(false);
            }else {
                getVideoGallery();
            }
            isLoaded = true;
        }
    }

    // get gallery
    private void getVideoGallery(){
        api.getItem("gallery/videogallery.php", Integer.parseInt(getArguments().getString("id")), Integer.parseInt(sessionManager.getCurrentlyLoggedUserId())).enqueue(new Callback<JsonObject>() {
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
                                    list.add(new Gallery(objData.optString("id"), objData.optString("thumbnail"), objData.optString("title"),
                                            objData.optString("video"), objData.optString("isFav"), type));
                                }
                            }
                        }else {
                            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                binding.tvNoData.setText(getString(R.string.no_video));
                galleryAdapter.notifyDataSetChanged();
                binding.pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get gallery
    private void getPhotoGallery(boolean isInsertList){
        api.getPhotoItem("gallery/gallery.php", Integer.parseInt(getArguments().getString("id")), Integer.parseInt(sessionManager.getCurrentlyLoggedUserId()),
                page, 20).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray arrData = jsonObject.optJSONArray("data");
                            if (arrData != null){
                                for (int i = 0;i < arrData.length();i++){
                                    JSONObject objData = (JSONObject) arrData.get(i);
                                    list.add(new Gallery(objData.optString("id"), objData.optString("image"), objData.optString("title"),
                                            objData.optString("video"), objData.optString("isFav"), type));
                                }
                            }
                        }else {
                            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
                binding.tvNoData.setText(getString(R.string.no_photo));
                if (isInsertList) {
                    galleryAdapter.notifyItemInserted(list.size());
                } else {
                    galleryAdapter.notifyDataSetChanged();
                }
                isLoadMore = true;
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