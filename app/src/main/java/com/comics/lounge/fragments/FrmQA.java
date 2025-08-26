package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.QAAdapter;
import com.comics.lounge.databinding.FrmQABinding;
import com.comics.lounge.modals.QA;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmQA extends Fragment {
    FrmQABinding binding;
    NewMain activity;
    List<QA> list, searchList;
    QAAdapter adapter;
    SessionManager sessionManager;

    public FrmQA() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmQABinding.inflate(getLayoutInflater());
        init();

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String strSearch = s.toString().trim();
                searchList.clear();
                if (strSearch.length() > 0){
                    for (QA qa : list){
                        if (qa.getQues().toLowerCase().contains(strSearch.toLowerCase())){
                            searchList.add(qa);
                        }
                    }
                    adapter = new QAAdapter(activity, searchList);
                }else {
                    adapter = new QAAdapter(activity, list);
                }
                binding.rcvQues.setAdapter(adapter);
            }
        });
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

    // init UI
    private void init(){
        sessionManager = new SessionManager(activity);
        list = new ArrayList<>();
        searchList = new ArrayList<>();
        adapter = new QAAdapter(activity, list);
        binding.rcvQues.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        AppUtil.rcvNoAnimator(binding.rcvQues);
        binding.rcvQues.setAdapter(adapter);
        if (Boolean.TRUE.equals(sessionManager.getCurrentUser().getMembership())){
            binding.btSubmit.setVisibility(View.VISIBLE);
            binding.etMess.setVisibility(View.VISIBLE);
        }else {
            binding.btSubmit.setVisibility(View.GONE);
            binding.etMess.setVisibility(View.GONE);
        }
        AppUtil.showLoading(activity);
        getQA();
    }

    // send message
    private void sendMess(String s) {
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.sendMess(Integer.parseInt(sessionManager.getCurrentlyLoggedUserId()), s).enqueue(new Callback<JsonObject>() {
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

    // get question and answer
    private void getQA(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getQA(1, 10, "").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        JSONArray arrData = jsonObject.optJSONArray("data");
                        if (arrData != null && arrData.length() > 0){
                            for (int  i = 0;i < arrData.length();i++){
                                JSONObject obj = (JSONObject) arrData.get(i);
                                list.add(new QA(obj.optString("id"), obj.optString("question")
                                        , obj.optString("answer"), true));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                AppUtil.hideLoading();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}