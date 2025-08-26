package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.comics.lounge.R;
import com.comics.lounge.adapter.pager.TabGalleryAdapter;
import com.comics.lounge.databinding.FrmGalleryBinding;
import com.google.android.material.tabs.TabLayout;

public class FrmGallery extends Fragment {
    FrmGalleryBinding binding;
    TabGalleryAdapter tabGalleryAdapter;
    public FrmGallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FrmGalleryBinding.inflate(getLayoutInflater());
        init();

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
        binding.vpGallery.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabGallery.selectTab(binding.tabGallery.getTabAt(position));
            }
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        tabGalleryAdapter = new TabGalleryAdapter(getChildFragmentManager(), getLifecycle());
        binding.tabGallery.addTab(binding.tabGallery.newTab().setText(getString(R.string.photos)));
        binding.tabGallery.addTab(binding.tabGallery.newTab().setText(getString(R.string.videos)));
        binding.tabGallery.addTab(binding.tabGallery.newTab().setText(getString(R.string.my_favorites)));
        binding.vpGallery.setAdapter(tabGalleryAdapter);
        binding.vpGallery.setOffscreenPageLimit(3);
    }
}