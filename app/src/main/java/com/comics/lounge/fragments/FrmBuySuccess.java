package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmBuySuccessBinding;


public class FrmBuySuccess extends Fragment {
    NewMain activity;
    FrmBuySuccessBinding binding;
    public FrmBuySuccess() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmBuySuccessBinding.inflate(getLayoutInflater());

        binding.tvTitle.setText("Congratulations, you have activated your "+getArguments().getString("name").replace("Purchase our ", ""));
        binding.btVideo.setOnClickListener(v -> activity.popupVideo());

        return binding.getRoot();
    }
}