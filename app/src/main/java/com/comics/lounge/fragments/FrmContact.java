package com.comics.lounge.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.conf.Constant;
import com.comics.lounge.databinding.FrmContactBinding;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmContact extends Fragment implements OnMapReadyCallback {
    FrmContactBinding binding;
    NewMain activity;
    SessionManager sessionManager;

    public FrmContact() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmContactBinding.inflate(getLayoutInflater());
        init();

        binding.edtMess.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int strLength = s.toString().trim().length();
                binding.tvEdtLength.setText(strLength + " / 100");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.btnSubmit.setOnClickListener(v -> {
            AppUtil.hideKeyboard(v);
            String s = binding.edtMess.getText().toString().trim();
            if (TextUtils.isEmpty(s)) {
                Toast.makeText(activity, getString(R.string.message_can_t_blank), Toast.LENGTH_SHORT).show();
            } else {
                AppUtil.showLoading(v.getContext());
                sendMess(s);
            }
        });
        binding.llAddress.setOnClickListener(v -> {
            String strUri = "http://maps.google.com/maps?q=loc:" + Constant.LATITUDE + "," + Constant.LONGITUDE + " (" + "The Comic's Lounge" + ")";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        });
        binding.llCall.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                phoneCall();
            }else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 9);
            }
        });
        binding.llEmail.setOnClickListener(v -> {
            String mailto = "mailto:"+getString(R.string.info_thecomicslounge_com_au);
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));
            startActivity(emailIntent);
        });
        binding.rlContact.setOnClickListener(v -> activity.addFrmDetail(new FrmContactOurTeam()));
        binding.rlFunEvent.setOnClickListener(v -> activity.addFrmDetail(new FrmFunEvent()));
        return binding.getRoot();
    }

    // phone call
    private void phoneCall(){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+getString(R.string._03_9348_9488) ));
        startActivity(intent);
    }

    // init UI
    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(activity);

        binding.rlMap.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(activity).widthPixels / 1.24)));
        binding.edtMess.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(activity).widthPixels / 1.24)));
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
                            binding.edtMess.setText("");
                            binding.edtMess.clearFocus();
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
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng latLng = new LatLng(Constant.LATITUDE, Constant.LONGITUDE);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.app_name)));
        marker.showInfoWindow();
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                phoneCall();
            }else{
                Toast.makeText(activity, R.string.permission_call_phone_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}