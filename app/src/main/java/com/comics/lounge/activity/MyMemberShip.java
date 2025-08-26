package com.comics.lounge.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.comics.lounge.R;
import com.comics.lounge.databinding.ActivityMyMemberShipBinding;
import com.comics.lounge.modals.user.User;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;

public class MyMemberShip extends AppCompatActivity {
    ActivityMyMemberShipBinding binding;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyMemberShipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.topBar.btBack.setOnClickListener(v -> finish());
        binding.topBar.btMenu.setOnClickListener(v -> startActivity(new Intent(this, Setting.class)));
    }

    // init UI
    private void init(){
        sessionManager = new SessionManager(this);
        User user = sessionManager.getCurrentUser();
        binding.tvMb.setText(user.getMembershipname());
        binding.tvPcDate.setText(AppUtil.fmNewDate(user.getStartDate()));
        binding.tvExpireDay.setText(user.getExpireInDays()+" days");
    }
}