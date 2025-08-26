package com.comics.lounge.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.Home;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmEnterTableBinding;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmEnterTable extends Fragment {
    FrmEnterTableBinding binding;
    NewMain activity;
    SessionManager sessionManager;
    public FrmEnterTable() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmEnterTableBinding.inflate(getLayoutInflater());

        binding.ivFortune.setLayoutParams(new LinearLayoutCompat.LayoutParams((int) (AppUtil.getScreenSize(activity).widthPixels / 1.305), (int) (AppUtil.getScreenSize(activity).widthPixels / 1.305)));
        binding.btOk.setOnClickListener(v -> {
            String no = binding.etTable.getText().toString().trim();
            if (TextUtils.isEmpty(no)){
                Toast.makeText(activity, getString(R.string.please_enter_table_number), Toast.LENGTH_SHORT).show();
            }else {
                AppUtil.showLoading(activity);
                addMember(no);
            }
        });
        return binding.getRoot();
    }

    // add member
    private void addMember(String no){
        sessionManager = new SessionManager(activity);
        RetroApi api = ComicsLoungeApp.getRetroApi();
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", sessionManager.getCurrentlyLoggedUserId());
        map.put("tableno", no);
        api.addMember("offers/add_member.php", map).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            AppUtil.setEntrySubmitted(activity, true);
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.DAY_OF_MONTH, 1);
                            c.set(Calendar.HOUR_OF_DAY, 1);
                            AppUtil.setResetTime(activity, c.getTime().getTime());
                            Toast.makeText(activity, jsonObject.optString("data"), Toast.LENGTH_SHORT).show();
                            activity.setResult(1257);
                            activity.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                AppUtil.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}