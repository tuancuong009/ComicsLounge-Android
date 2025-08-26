package com.comics.lounge.adapter.pager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.comics.lounge.fragments.FrmGoldOffer;
import com.comics.lounge.fragments.FrmSpecialOffer;

import java.util.List;

public class OfferPagerAdapter extends FragmentStateAdapter {
    List<String> list;

    public OfferPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<String> list) {
        super(fragmentManager, lifecycle);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment frm;
        Bundle bundle = new Bundle();
        bundle.putString("id", list.get(position));
        if (position == 0){
            frm = new FrmSpecialOffer();
        }else {
            frm = new FrmGoldOffer();
        }
        frm.setArguments(bundle);
        return frm;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
