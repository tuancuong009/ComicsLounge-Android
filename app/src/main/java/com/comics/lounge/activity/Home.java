package com.comics.lounge.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.adapter.HomeMenuAdapter;
import com.comics.lounge.databinding.ActivityHomeBinding;
import com.comics.lounge.databinding.PopupEnterTableBinding;
import com.comics.lounge.modals.HomeMenu;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomSpace;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.intercom.android.sdk.identity.Registration;
import io.sentry.util.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    List<HomeMenu> list;
    HomeMenuAdapter adapter;
    int doubleBackToExit = 1;
    SessionManager sessionManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExit == 2) {
                    finishAffinity();
                    System.exit(0);
                } else {
                    doubleBackToExit++;
                    Toast.makeText(Home.this, R.string.please_press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(() -> doubleBackToExit = 1, 2000);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
        binding.topBar.btMenu.setOnClickListener(v -> startActivity(new Intent(this, Setting.class).putExtra("from", "home")));
        binding.btTable.setOnClickListener(v -> {
            binding.btTable.setCardBackgroundColor(getColor(R.color.bg_splash));
            binding.tvTable.setTextColor(getColor(R.color.white));
            new Handler().postDelayed(() -> {
                binding.btTable.setCardBackgroundColor(getColor(R.color.white));
                binding.tvTable.setTextColor(getColor(R.color.bg_splash));
            }, 10);
            Intent it = new Intent(this, NewMain.class);
            it.putExtra("screen", "table");
            startActivityForResult(it, 1257);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupList();
    }

    // init UI
    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        sessionManager = new SessionManager(this);
        binding.topBar.btBack.setVisibility(View.GONE);
        list = new ArrayList<>();
        adapter = new HomeMenuAdapter(list, this);
        binding.rcvMenu.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rcvMenu.setAdapter(adapter);
        AppUtil.rcvNoAnimator(binding.rcvMenu);
        if (AppUtil.getEntrySubmitted(this)){
            if (new Date().getTime() < AppUtil.getResetTime(this)){
                binding.tvTable.setText(R.string.entry_submitted_good_luck);
                binding.tvTable.setTextColor(getColor(R.color.dark_blue));
                binding.btTable.setForeground(getDrawable(R.drawable.bg_bt_submited));
                binding.btTable.setEnabled(false);
            }else {
                AppUtil.setEntrySubmitted(this, false);
            }
        }
        if (getIntent().getStringExtra("intercom") != null){
            Registration registration = Registration.create().withUserId(sessionManager.getCurrentlyLoggedUserId());
            Intercom.client().loginIdentifiedUser(registration, new IntercomStatusCallback() {
                @Override
                public void onSuccess() {
                    // Handle success
                }

                @Override
                public void onFailure(@NonNull IntercomError intercomError) {
                    // Handle failure
                }
            });
            Intercom.client().present(IntercomSpace.Home);
        }
    }

    // set up list
    private void setupList(){
        list.clear();
        list.add(new HomeMenu(R.mipmap.ic_calendar, getString(R.string.calendar)));
        list.add(new HomeMenu(R.mipmap.ic_menu, getString(R.string.menu)));
        list.add(new HomeMenu(R.mipmap.ic_person, getString(R.string.my_account)));
        if (sessionManager.getCurrentUser().getMembership()){
            list.add(new HomeMenu(R.mipmap.ic_mb, getString(R.string.my_membership_str)));
        }else {
            list.add(new HomeMenu(R.mipmap.ic_join, getString(R.string.become_a_member)));
        }
        list.add(new HomeMenu(R.mipmap.ic_gallery, getString(R.string.gallery)));
        list.add(new HomeMenu(R.mipmap.ic_offers, getString(R.string.offers)));
        list.add(new HomeMenu(R.mipmap.ic_contacts, getString(R.string.contacts)));
        list.add(new HomeMenu(R.mipmap.ic_concierge, getString(R.string.concierge)));
        adapter.notifyDataSetChanged();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                String token = task1.getResult();
                Log.e("TAG", "token: " + token);
            }
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, getString(R.string.permission_notifications_denied), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1257){
            binding.tvTable.setText(getString(R.string.entry_submitted_good_luck));
            binding.tvTable.setTextColor(getColor(R.color.dark_blue));
            binding.btTable.setForeground(getDrawable(R.drawable.bg_bt_submited));
            binding.btTable.setEnabled(false);
        }
    }
}