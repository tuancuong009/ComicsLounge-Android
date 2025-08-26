package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmContactOurTeamBinding;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmContactOurTeam extends Fragment {
    FrmContactOurTeamBinding binding;
    NewMain activity;
    public FrmContactOurTeam() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmContactOurTeamBinding.inflate(getLayoutInflater());

        binding.btSubmit.setOnClickListener(v -> {
            AppUtil.hideKeyboard(v);
            String s = binding.etMess.getText().toString().trim();
            if (TextUtils.isEmpty(s)) {
                Toast.makeText(activity, getString(R.string.message_can_t_blank), Toast.LENGTH_SHORT).show();
            } else {
                AppUtil.showLoading(v.getContext());
                sendMess(s);
            }
        });
        return binding.getRoot();
    }

    // send message
    private void sendMess(String s) {
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.sendMess(Integer.parseInt(activity.sessionManager.getCurrentlyLoggedUserId()), s).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()) {
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("Success")) {
                            binding.etMess.setText("");
                            binding.etMess.clearFocus();
                        }
                        Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        activity.getOnBackPressedDispatcher().onBackPressed();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                AppUtil.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppUtil.hideLoading();
            }
        });
    }
}