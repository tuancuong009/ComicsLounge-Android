package com.comics.lounge.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.databinding.ActivitySettingBinding;
import com.comics.lounge.databinding.PopupMemberVideoBinding;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomSpace;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.intercom.android.sdk.identity.Registration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Setting extends AbstractBaseActivity {
    ActivitySettingBinding binding;
    String from;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.topBar.btBack.setOnClickListener(v -> finish());
        binding.rlAcc.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "account");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "account"));
            }
        });
        binding.rlTerm.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "term");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "term"));
            }
        });
        binding.rlContact.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "contact");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "contact"));
            }
        });
        binding.rlCalendar.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "calendar");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "calendar"));
            }
        });
        binding.rlClaimMember.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "claim_mb");
                setResult(Constant.SETTING_CODE, it);
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "claim_mb"));
                finish();
            }
        });
        binding.rlSaved.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "saved");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "saved"));
            }
        });
        binding.rlTicket.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "confirmed");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "confirmed"));
            }
        });
        binding.rlHistory.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "history");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "history"));
            }
        });
        binding.rlBuyMember.setOnClickListener(v -> {
            if (from.equals("main")) {
                Intent it = new Intent();
                it.putExtra("screen", "become_membership");
                setResult(Constant.SETTING_CODE, it);
                finish();
            }else {
                startActivity(new Intent(this, NewMain.class).putExtra("screen", "become_membership"));
            }
        });
        binding.rlMemberVideo.setOnClickListener(v -> popupVideo());
        binding.btLogout.setOnClickListener(v -> popupLogout());
        binding.rlNotifications.setOnClickListener(v -> {
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
        });
        binding.btDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_account))
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteAcc();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show());
    }

    // init UI
    private void init(){
        from = getIntent().getStringExtra("from");
        binding.topBar.btMenu.setVisibility(View.GONE);
        sessionManager = new SessionManager(this);
        binding.rlNotifications.setVisibility(Boolean.TRUE.equals(sessionManager.getCurrentUser().getMembership()) ? View.GONE : View.VISIBLE);
        binding.tvUserName.setText(sessionManager.getCurrentUser().getName());
    }

    // popup logout
    public void popupLogout() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("Are you sure want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            sessionManager.logoutUser();
            Intercom.client().logout();
            Intent intent = new Intent(Setting.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // delete account
    private void deleteAcc(){
        AppUtil.showLoading(this);
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.deleteAcc(sessionManager.getCurrentlyLoggedUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            sessionManager.logoutUser();
                            Intercom.client().logout();
                            Intent intent = new Intent(Setting.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        Toast.makeText(Setting.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                AppUtil.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {
                AppUtil.hideLoading();
                Toast.makeText(Setting.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}