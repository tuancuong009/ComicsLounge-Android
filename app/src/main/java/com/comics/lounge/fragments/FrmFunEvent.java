package com.comics.lounge.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmFunEventBinding;
import com.comics.lounge.databinding.PopupSubmitFunEventBinding;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.CustomSpinnerAdapter;
import com.google.gson.JsonObject;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmFunEvent extends Fragment {
    FrmFunEventBinding binding;
    NewMain activity;
    int year, month, day;
    String type = "Corporate", guest = "", cate = "";
    public FrmFunEvent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmFunEventBinding.inflate(getLayoutInflater());
        init();

        binding.btCorporate.setOnClickListener(v -> {
            binding.btCorporate.setBackgroundResource(R.drawable.bg_button);
            binding.btCorporate.setTextColor(activity.getColor(R.color.white));
            binding.btPrivate.setBackgroundResource(R.drawable.bg_bt_logout);
            binding.btPrivate.setTextColor(activity.getColor(R.color.bg_splash));
            type = "Corporate";
            binding.llErrType.setVisibility(View.GONE);
        });
        binding.btPrivate.setOnClickListener(v -> {
            binding.btPrivate.setBackgroundResource(R.drawable.bg_button);
            binding.btPrivate.setTextColor(activity.getColor(R.color.white));
            binding.btCorporate.setBackgroundResource(R.drawable.bg_bt_logout);
            binding.btCorporate.setTextColor(activity.getColor(R.color.bg_splash));
            type = "Private";
            binding.llErrType.setVisibility(View.GONE);
        });
        binding.spinnerNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(activity.getColor(R.color.grey_3));
                }else {
                    String[] arr = activity.getResources().getStringArray(R.array.guest_number);
                    guest = arr[position];
                    binding.llErrGuest.setVisibility(View.GONE);
                    binding.rlGuest.setBackgroundResource(R.drawable.bg_edt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    ((TextView) view).setTextColor(activity.getColor(R.color.grey_3));
                }else {
                    String[] arr = activity.getResources().getStringArray(R.array.cate_arr);
                    cate = arr[position];
                    binding.llErrCate.setVisibility(View.GONE);
                    binding.rlCate.setBackgroundResource(R.drawable.bg_edt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    activity,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        day = dayOfMonth;
                        month = monthOfYear;
                        year = year1;
                        binding.etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);

                    },
                    year, month, day);
            datePickerDialog.show();
        });
        binding.rlDate.setOnClickListener(v -> binding.etDate.performClick());
        binding.btSubmit.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String date = binding.etDate.getText().toString().trim();
            if (type.equals("")){
                binding.llErrType.setVisibility(View.VISIBLE);
                binding.nsv.postDelayed(() -> binding.nsv.scrollTo(0, binding.llType.getTop()), 100);
            }else if (guest.equals("")){
                binding.llErrGuest.setVisibility(View.VISIBLE);
                binding.rlGuest.setBackgroundResource(R.drawable.bg_edt_error);
            }else if (cate.equals("")){
                binding.llErrCate.setVisibility(View.VISIBLE);
                binding.rlCate.setBackgroundResource(R.drawable.bg_edt_error);
            }else {
                AppUtil.showLoading(activity);
                submit(date, name, phone);
            }
        });

        return binding.getRoot();
    }

    // init UI
    private void init(){
        CustomSpinnerAdapter guestAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, activity.getResources().getStringArray(R.array.guest_number));
        guestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerNum.setAdapter(guestAdapter);

        CustomSpinnerAdapter cateAdapter = new CustomSpinnerAdapter(activity, android.R.layout.simple_spinner_item, activity.getResources().getStringArray(R.array.cate_arr));
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCate.setAdapter(cateAdapter);
        binding.etName.setText(activity.sessionManager.getCurrentUser().getName());
        binding.etPhone.setText(activity.sessionManager.getCurrentUser().getMobile());
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    // popup submit success
    private void popupSuccess(){
        Dialog dialog = new Dialog(activity);
        PopupSubmitFunEventBinding funEventBinding = PopupSubmitFunEventBinding.inflate(getLayoutInflater());
        dialog.setContentView(funEventBinding.getRoot());
        AppUtil.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);

        funEventBinding.btClose.setOnClickListener(v -> {
            dialog.dismiss();
            activity.getOnBackPressedDispatcher().onBackPressed();
        });

        dialog.show();
    }

    // submit function and events
    private void submit(String date, String name, String phone){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.submitFunEvent(activity.sessionManager.getCurrentlyLoggedUserId(), date, type, guest, cate, name, phone).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("Success")){
                            popupSuccess();
                        }else {
                            Toast.makeText(activity, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
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
}