package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmMbDetailBinding;
import com.comics.lounge.modals.user.User;
import com.comics.lounge.modals.user.UserResponse;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmMbDetail extends Fragment {
    FrmMbDetailBinding binding;
    NewMain activity;
    String crEmail, crPhone;
    User user;
    FrmMyAccount frmMyAccount;

    public FrmMbDetail(FrmMyAccount frmMyAccount) {
        this.frmMyAccount = frmMyAccount;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmMbDetailBinding.inflate(getLayoutInflater());
        init();

        binding.btEditPhone.setOnClickListener(v -> {
            clearFocusEmail();
            AppUtil.focusKeyboard(binding.etPhone, activity);
            binding.btEditPhone.setVisibility(View.GONE);
        });
        binding.btEditEmail.setOnClickListener(v -> {
            clearFocusPhone();
            AppUtil.focusKeyboard(binding.etEmail, activity);
            binding.btEditEmail.setVisibility(View.GONE);
        });
        binding.llMain.setOnClickListener(v -> {
            clearFocusPhone();
            clearFocusEmail();
        });
        KeyboardVisibilityEvent.setEventListener(activity, b -> {
            if (!b) {
                if (binding.etPhone.isFocused()){
                    clearFocusPhone();
                }
                if (binding.etEmail.isFocused()){
                    clearFocusEmail();
                }
            }
        });
        binding.etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String email = v.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    binding.ipEmail.setError(activity.getString(R.string.this_field_is_required));
                }else if (!AppUtil.isValidEmail(email)){
                    binding.ipEmail.setError(activity.getString(R.string.enter_valid_email));
                }else if (!email.equals(crEmail)){
                    AppUtil.showLoading(activity);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("user_id", user.getUserId());
                    map.put("email", email);
                    update("updatemail.php", map);
                    crEmail = email;
                }else {
                    AppUtil.hideKeyboard(v);
                }
                return true;
            }
            return false;
        });
        binding.etPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String mobile = v.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)){
                    binding.ipPhone.setError(getString(R.string.this_field_is_required));
                }else if (!PhoneNumberUtils.isGlobalPhoneNumber(mobile) || mobile.length() < 8){
                    binding.ipPhone.setError(getString(R.string.please_enter_valid_phone_number));
                }else if (!mobile.equals(crPhone)){
                    AppUtil.showLoading(activity);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("user_id", user.getUserId());
                    map.put("phone", mobile);
                    update("updatephone.php", map);
                    crPhone = mobile;
                }else {
                    AppUtil.hideKeyboard(v);
                }
                return true;
            }
            return false;
        });
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 8){
                    binding.ipPhone.setError(null);
                }
            }
        });
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0){
                    binding.ipEmail.setError(null);
                }
            }
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        user = activity.sessionManager.getCurrentUser();
        binding.tvName.setText(user.getName());
        binding.etPhone.setText(user.getMobile());
        binding.etEmail.setText(user.getEmail());
        crEmail = user.getEmail();
        crPhone = user.getMobile();
    }

    // clear focus email
    private void clearFocusEmail(){
        binding.etEmail.clearFocus();
        binding.btEditEmail.setVisibility(View.VISIBLE);
        binding.etEmail.setFocusableInTouchMode(false);
    }

    // clear focus phone number
    private void clearFocusPhone(){
        binding.etPhone.clearFocus();
        binding.btEditPhone.setVisibility(View.VISIBLE);
        binding.etPhone.setFocusableInTouchMode(false);
    }

    // update profile
    private void update(String url, HashMap<String, Object> map){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.updateUser(url, map).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        if (jsonObject.optString("status").equals("Success")){
                            saveUserPf();
                        }
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

    // save user profile
    private void saveUserPf(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getUserPf(Integer.parseInt(user.getUserId())).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONObject jsParams = jsonObject.optJSONObject("params");
                            if (jsParams != null){
                                activity.sessionManager.createOrUpdateLogin(jsParams);
                                frmMyAccount.init();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}