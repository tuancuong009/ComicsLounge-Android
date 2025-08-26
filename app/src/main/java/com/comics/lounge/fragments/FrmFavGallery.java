package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.GalleryAdapter;
import com.comics.lounge.databinding.FrmFavGalleryBinding;
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


public class FrmFavGallery extends Fragment {
    FrmFavGalleryBinding binding;
    NewMain activity;
    GalleryAdapter galleryAdapter;
    List<Gallery> list;
    RetroApi api = ComicsLoungeApp.getRetroApi();
    SessionManager sessionManager;

    public FrmFavGallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmFavGalleryBinding.inflate(getLayoutInflater());
        init();

        binding.swRefresh.setOnRefreshListener(() -> {
            if (getArguments().getString("type").equals("photo")){
                getFavGallery("gallery/favcomediangallery.php", "photo");
            }else {
                getFavGallery("gallery/favcomedianvideo.php", "video");
            }
            binding.swRefresh.setRefreshing(false);
        });

        return binding.getRoot();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible){
            if (getArguments().getString("type").equals("photo")){
                getFavGallery("gallery/favcomediangallery.php", "photo");
            }else {
                getFavGallery("gallery/favcomedianvideo.php", "video");
            }
        }
    }

    // init UI
    private void init(){
        sessionManager = new SessionManager(activity);
        binding.swRefresh.setColorSchemeColors(activity.getColor(R.color.bg_splash));
        binding.rcvGallery.setLayoutManager(new GridLayoutManager(activity, 2));
        AppUtil.rcvNoAnimator(binding.rcvGallery);
        list = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(activity, list, true, binding.tvNoData);
        binding.rcvGallery.setAdapter(galleryAdapter);
    }

    // get gallery
    private void getFavGallery(String url, String type){
        api.getFavoriteGallery(url, Integer.parseInt(sessionManager.getCurrentlyLoggedUserId())).enqueue(new Callback<JsonObject>() {
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
                                String img;
                                for (int i = 0;i < arrData.length();i++){
                                    JSONObject objData = (JSONObject) arrData.get(i);
                                    if (type.equals("photo")){
                                        img = objData.optString("image");
                                    }else {
                                        img = objData.optString("thumbnail");
                                    }
                                    list.add(new Gallery(objData.optString("id"), img, objData.optString("title"),
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
                if (type.equals("photo")){
                    binding.tvNoData.setText("No favorite photos");
                }else {
                    binding.tvNoData.setText("No favorite videos");
                }
                binding.tvNoData.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);
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
}