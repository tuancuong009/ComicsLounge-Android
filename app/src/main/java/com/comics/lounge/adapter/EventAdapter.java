package com.comics.lounge.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.fragments.FrmEventDetail;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.ItemEventNewBinding;
import com.comics.lounge.modals.EventNew;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {
    NewMain activity;
    List<EventNew> list;

    public EventAdapter(NewMain activity, List<EventNew> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventNewBinding binding = ItemEventNewBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        EventNew eventNew = list.get(position);
        holder.bind.tvName.setText(eventNew.getName());
        holder.bind.cbLike.setChecked(eventNew.isFav());
        if (eventNew.getStDate().equals(eventNew.getEndDate())){
            holder.bind.tvTime.setText(AppUtil.fmNewDateTime(eventNew.getStDate()));
        }else {
            holder.bind.tvTime.setText(AppUtil.fmNewDateTime(eventNew.getStDate())+" - "+AppUtil.fmNewDateTime(eventNew.getEndDate()));
        }
        holder.bind.llGold.setVisibility(eventNew.isMemberAccess() ? View.VISIBLE : View.GONE);
        Glide.with(activity).load(eventNew.getImg()).error(R.drawable.img_event).centerCrop().into(holder.bind.iv);
        holder.itemView.setOnClickListener(v -> {
            activity.frmEventDetail = new FrmEventDetail();
            Bundle bundle = new Bundle();
            bundle.putString("id", eventNew.getId());
            activity.frmEventDetail.setArguments(bundle);
            activity.addFrmDetail(activity.frmEventDetail);
        });
//        holder.bind.cbLike.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            SessionManager sessionManager = new SessionManager(activity);
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("user_id", sessionManager.getCurrentlyLoggedUserId());
//            map.put("event_id", eventNew.getId());
//            if (isChecked) {
//                addOrRemove("event/add_fav_event.php", map);
//            } else {
//                addOrRemove("event/remove_fav_event.php", map);
//            }
//        });
        holder.bind.cbLike.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(activity);
            HashMap<String, Object> map = new HashMap<>();
            map.put("user_id", sessionManager.getCurrentlyLoggedUserId());
            map.put("event_id", eventNew.getId());
            if (eventNew.isFav()){
                addOrRemove("event/remove_fav_event.php", map);
            }else {
                addOrRemove("event/add_fav_event.php", map);
            }
            eventNew.setFav(!eventNew.isFav());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemEventNewBinding bind;
        public VH(@NonNull ItemEventNewBinding itemEventNewBinding) {
            super(itemEventNewBinding.getRoot());
            this.bind = itemEventNewBinding;
        }
    }

    // add or remove favorite gallery
    private void addOrRemove(String url, HashMap<String, Object> map) {
        RetroApi api = ComicsLoungeApp.getRetroApi();
        api.addOrRemoveGallery(url, map).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JSONObject jsonObject;
                if (response.isSuccessful()) {
                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));
                        Toast.makeText(activity, jsonObject.optString("data"), Toast.LENGTH_SHORT).show();
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
