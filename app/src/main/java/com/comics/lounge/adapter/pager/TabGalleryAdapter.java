package com.comics.lounge.adapter.pager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.comics.lounge.fragments.FrmMyFavorites;
import com.comics.lounge.fragments.FrmTabGallery;

public class TabGalleryAdapter extends FragmentStateAdapter {
    public TabGalleryAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment frm;
        Bundle bundle = new Bundle();
        if (position == 0){
            frm = new FrmTabGallery();
            bundle.putString("type", "photo");
            frm.setArguments(bundle);
        }else if (position == 1){
            frm = new FrmTabGallery();
            bundle.putString("type", "video");
            frm.setArguments(bundle);
        }else {
            frm = new FrmMyFavorites();
        }
        return frm;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
