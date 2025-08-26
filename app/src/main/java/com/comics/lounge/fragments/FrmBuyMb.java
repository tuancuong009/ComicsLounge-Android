package com.comics.lounge.fragments;

import static com.comics.lounge.conf.Constant.REFRESH_AFTER_PAY;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.activity.PromoCodeActivity;
import com.comics.lounge.databinding.FrmBuyMbBinding;
import com.comics.lounge.modals.Membership;
import com.comics.lounge.utils.AppUtil;


public class FrmBuyMb extends Fragment {
    FrmBuyMbBinding binding;
    NewMain activity;
    Membership mb;
    public FrmBuyMb() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmBuyMbBinding.inflate(getLayoutInflater());
        init();

        binding.btBuy.setOnClickListener(v -> {
            Intent intent = new Intent(activity, PromoCodeActivity.class);
            intent.putExtra("membership", mb);
            activity.startActivityForResult(intent, REFRESH_AFTER_PAY);
        });
        return binding.getRoot();
    }

    // init UI
    private void init(){
        mb = (Membership) getArguments().getSerializable("data");
        binding.tvName.setText(mb.getName());
        binding.tvDes.setText(Html.fromHtml(mb.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        binding.tvPrice.setText("$"+mb.getPrice());
        Glide.with(activity).load(mb.getImage()).fitCenter().into(binding.ivMember);
        binding.ivMember.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(activity).widthPixels/2.45)));
    }
}