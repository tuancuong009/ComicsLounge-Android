package com.comics.lounge.fragments;

import static androidx.appcompat.content.res.AppCompatResources.getColorStateList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmCheckoutBinding;
import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.utils.NumberUtils;


public class FrmCheckout extends Fragment {
    FrmCheckoutBinding binding;
    NewMain activity;
    public FrmCheckout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmCheckoutBinding.inflate(getLayoutInflater());
        init();

        binding.btPay.setOnClickListener(v -> activity.addFrmDetail(new FrmPayment()));

        return binding.getRoot();
    }

    // init UI
    private void init(){
        if (getArguments() != null){
            binding.tvDate.setText(DatesUtils.fmDate2(getArguments().getString("date"))+" "+getArguments().getString("open_time"));
            binding.tvPriceShow.setText(getArguments().getString("show"));
            binding.tvPriceMeal.setText(getArguments().getString("meal"));
            binding.tvPriceTotal.setText(getArguments().getString("total"));
        }
    }
}