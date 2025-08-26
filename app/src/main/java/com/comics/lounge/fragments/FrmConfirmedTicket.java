package com.comics.lounge.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.adapter.TicketAdapter;
import com.comics.lounge.databinding.FrmConfirmedTicketBinding;
import com.comics.lounge.modals.TicketHistory;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.DatesUtils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrmConfirmedTicket extends Fragment {
    FrmConfirmedTicketBinding binding;
    TicketAdapter adapter;
    List<TicketHistory> list;
    NewMain activity;
    int showTicket, mealTicket;
    String name, orderDate, orderTime, orderId;
    SessionManager sessionManager;
    public FrmConfirmedTicket() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmConfirmedTicketBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        sessionManager = new SessionManager(activity);
        list = new ArrayList<>();
        adapter = new TicketAdapter("confirm", activity, list);
        binding.rcvTicket.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rcvTicket.setAdapter(adapter);
        getData();
    }

    // get booking history
    private void getData(){
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.getBookingHistory("orders/orderlist.php", sessionManager.getCurrentlyLoggedUserId()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()){
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        if (jsonObject.optString("status").equals("success")){
                            JSONArray arrOrder = jsonObject.optJSONArray("orders");
                            if (arrOrder != null && arrOrder.length() > 0){
                                orderId = "";
                                for (int i = 0;i < arrOrder.length();i++){
                                    JSONObject objOrd = (JSONObject) arrOrder.get(i);
                                    orderId = objOrd.optString("order_id");
                                    JSONArray arrItem = objOrd.optJSONArray("items");
                                    showTicket = mealTicket = 0;
                                    name = orderDate = orderTime = "";
                                    if (arrItem != null && arrItem.length() > 0){
                                        for (int j = 0;j < arrItem.length();j++){
                                            JSONObject objItem = (JSONObject) arrItem.get(j);
                                            name = objItem.optString("product_name");
                                            orderDate = objItem.optString("event_date");
                                            orderTime = objItem.optString("showtime");
                                            if (objItem.optString("show_type").equals("Show Only")){
                                                showTicket++;
                                            }
                                            if (objItem.optString("show_type").equals("Show with meal")){
                                                mealTicket++;
                                            }
                                        }
                                    }
                                    list.add(new TicketHistory(orderId, name, orderDate, orderTime, showTicket, mealTicket));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
                binding.pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.pbLoading.setVisibility(View.GONE);
                Toast.makeText(activity, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}