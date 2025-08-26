package com.comics.lounge.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.pager.FavPagerAdapter;
import com.comics.lounge.databinding.FrmMyFavoritesBinding;


public class FrmMyFavorites extends Fragment {
    FrmMyFavoritesBinding binding;
    NewMain activity;
    FavPagerAdapter favPagerAdapter;

    boolean isLoaded = false;
    public FrmMyFavorites() {
        // Required empty public constructor
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmMyFavoritesBinding.inflate(getLayoutInflater());

        binding.vpGallery.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0){
                    binding.rbPhoto.setChecked(true);
                }else {
                    binding.rbVideo.setChecked(true);
                }
            }
        });
        binding.rgTab.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rb_photo) {
                    binding.vpGallery.setCurrentItem(0);
                } else {
                    binding.vpGallery.setCurrentItem(1);
                }
        });

        return binding.getRoot();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible){
            init();
        }
    }

    // init UI
    private void init(){
        favPagerAdapter = new FavPagerAdapter(getChildFragmentManager(), getLifecycle());
        binding.vpGallery.setAdapter(favPagerAdapter);
        binding.vpGallery.setOffscreenPageLimit(2);
    }
}