package com.comics.lounge.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.ForgotPasswordActivity;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmChangePwBinding;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmChangePw extends Fragment {
    FrmChangePwBinding binding;
    NewMain activity;
    SessionManager sessionManager;
    public FrmChangePw() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmChangePwBinding.inflate(getLayoutInflater());

        binding.edtOldPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()){
                    binding.ipOldPw.setError(null);
                    binding.edtOldPw.setBackgroundResource(R.drawable.bg_edt);
                    binding.tvOldPwLabel.setTextColor(activity.getColor(R.color.gray_1));
                }
            }
        });
        binding.edtNewPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0){
                    binding.ipNewPw.setError(null);
                    binding.edtNewPw.setBackgroundResource(R.drawable.bg_edt);
                    binding.tvNewPwLabel.setTextColor(activity.getColor(R.color.gray_1));
                }
            }
        });
        binding.edtCfPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()){
                    binding.ipCfPw.setError(null);
                    binding.edtCfPw.setBackgroundResource(R.drawable.bg_edt);
                    binding.tvCfPwLabel.setTextColor(activity.getColor(R.color.gray_1));
                }
            }
        });
        binding.btSubmit.setOnClickListener(v -> {
            String oldPw = binding.edtOldPw.getText().toString().trim();
            String newPw = binding.edtNewPw.getText().toString().trim();
            String cfPw = binding.edtCfPw.getText().toString().trim();
            if (AppUtil.edtBlank(binding.edtOldPw, binding.ipOldPw, getString(R.string.password_cannot_be_blank))) {
                binding.tvOldPwLabel.setTextColor(activity.getColor(R.color.red));
            }else if (!AppUtil.isValidPwd(binding.edtOldPw.getText().toString().trim())) {
                binding.ipOldPw.setError(getString(R.string.password_validation));
                binding.tvOldPwLabel.setTextColor(activity.getColor(R.color.red));
            }else if (AppUtil.edtBlank(binding.edtNewPw, binding.ipNewPw, getString(R.string.password_cannot_be_blank))) {
                binding.tvNewPwLabel.setTextColor(activity.getColor(R.color.red));
            }else if (!AppUtil.isValidPwd(binding.edtNewPw.getText().toString().trim())) {
                binding.ipNewPw.setError(getString(R.string.password_validation));
                binding.tvNewPwLabel.setTextColor(activity.getColor(R.color.red));
            }else if (newPw.equals(oldPw)){
                binding.ipNewPw.setError(getString(R.string.the_new_password_cannot_be_the_same_as_the_old_password));
                binding.tvNewPwLabel.setTextColor(activity.getColor(R.color.red));
            }else if (AppUtil.edtBlank(binding.edtCfPw, binding.ipCfPw, getString(R.string.password_cannot_be_blank))) {
                binding.tvCfPwLabel.setTextColor(activity.getColor(R.color.red));
            }else if (!cfPw.equals(newPw)){
                binding.ipCfPw.setError(getString(R.string.the_new_password_doesn_t_match));
                binding.tvCfPwLabel.setTextColor(activity.getColor(R.color.red));
            }else {
                AppUtil.showLoading(activity);
                changePw(oldPw, newPw);
            }
        });
        binding.tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(activity, ForgotPasswordActivity.class)));

        return binding.getRoot();
    }

    // change password
    private void changePw(String oldPw, String newPw){
        sessionManager = new SessionManager(activity);
        RetroApi api = ComicsLoungeApp.getRetroApi();
        HashMap<String, Object> body = new HashMap<>();
        body.put("customer_id", sessionManager.getCurrentUser().getUserId());
        body.put("current_password", oldPw);
        body.put("new_password", newPw);
        api.changePw(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        AppUtil.setPw(activity, newPw);
                        activity.isChangePw = true;
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
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}