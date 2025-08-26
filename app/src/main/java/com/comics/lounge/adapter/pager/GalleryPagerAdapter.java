package com.comics.lounge.adapter.pager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.comics.lounge.fragments.FrmGallery;
import com.comics.lounge.fragments.FrmGalleryDetail;
import com.comics.lounge.fragments.FrmMyFavorites;
import com.comics.lounge.modals.Categories;

import java.util.List;

public class GalleryPagerAdapter extends FragmentStateAdapter {
    List<Categories> list;
    String type;

    public GalleryPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Categories> list, String type) {
        super(fragmentManager, lifecycle);
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment frm = new FrmGalleryDetail();
        Bundle bundle = new Bundle();
        bundle.putString("id", list.get(position).getId());
        bundle.putString("type", type);
        frm.setArguments(bundle);
        return frm;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
