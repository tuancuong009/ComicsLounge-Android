package com.comics.lounge.adapter.pager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.comics.lounge.fragments.FrmFood;
import com.comics.lounge.modals.Categories;

import java.util.List;

public class TabMenuAdapter extends FragmentStateAdapter {
    List<Categories> list;

    public TabMenuAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Categories> list) {
        super(fragmentManager, lifecycle);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        FrmFood frm = new FrmFood();
        Bundle bundle = new Bundle();
        bundle.putString("id", list.get(position).getId());
        frm.setArguments(bundle);
        return frm;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
