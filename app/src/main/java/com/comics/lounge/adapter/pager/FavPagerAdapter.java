package com.comics.lounge.adapter.pager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.comics.lounge.fragments.FrmFavGallery;
import com.comics.lounge.modals.Categories;

import java.util.List;

public class FavPagerAdapter  extends FragmentStateAdapter {

    public FavPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment frm = new FrmFavGallery();
        Bundle bundle = new Bundle();
        bundle.putString("type", position == 0 ? "photo" : "video");
        frm.setArguments(bundle);
        return frm;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
