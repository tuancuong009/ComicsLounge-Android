package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.databinding.FrmTermBinding;
import com.comics.lounge.utils.AppUtil;


public class FrmTerm extends Fragment {
    FrmTermBinding binding;
    NewMain activity;
    public FrmTerm() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmTermBinding.inflate(getLayoutInflater());

        binding.tvTermDes7.setText(Html.fromHtml(activity.getString(R.string.term_des_7), Html.FROM_HTML_MODE_COMPACT));
        binding.tvTermDes14.setText(Html.fromHtml(activity.getString(R.string.term_des_14), Html.FROM_HTML_MODE_COMPACT));
        binding.tvTermDes15.setText(Html.fromHtml(activity.getString(R.string.term_des_15), Html.FROM_HTML_MODE_COMPACT));

        return binding.getRoot();
    }
}