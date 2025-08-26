package com.comics.lounge.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.VideoPlayer;
import com.comics.lounge.databinding.ItemVideoBinding;
import com.comics.lounge.databinding.PopupImgOptionBinding;
import com.comics.lounge.modals.Gallery;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.VH> {
    Context context;
    List<Gallery> list;
    int crPos = 0;
    boolean isFav;
    TextView tvNoData;
    SessionManager sessionManager;

    public GalleryAdapter(Context context, List<Gallery> list, boolean isFav) {
        this.context = context;
        this.list = list;
        this.isFav = isFav;
    }

    public GalleryAdapter(Context context, List<Gallery> list, boolean isFav, TextView tvNoData) {
        this.context = context;
        this.list = list;
        this.isFav = isFav;
        this.tvNoData = tvNoData;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        sessionManager = new SessionManager(context);
        SimpleDateFormat fm = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Gallery gallery = list.get(position);
        holder.bind.tvName.setText(gallery.getName());
        holder.bind.itemPhoto.setVisibility(gallery.getType().equals("photo") ? View.VISIBLE : View.GONE);
        holder.bind.itemVideo.setVisibility(gallery.getType().equals("video") ? View.VISIBLE : View.GONE);
        if (gallery.getType().equals("photo")){
            Glide.with(context).asBitmap().load(gallery.getImg()).fitCenter().into(holder.bind.ivPhoto);
//            new Handler().postDelayed(() -> holder.bind.itemPhoto.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    (int) (holder.bind.itemPhoto.getMeasuredWidth() / 1.38))), 100);
            holder.bind.itemPhoto.setOnClickListener(v -> popupSelectImg(position));
            holder.bind.cbFavPhoto.setChecked(gallery.getIsFav().equals("true"));
            holder.bind.cbFavPhoto.setOnClickListener(v -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("user_id", sessionManager.getCurrentlyLoggedUserId());
                map.put("imageid", gallery.getId());
                if (isFav){
                    list.remove(position);
                    addOrRemove("gallery/removefavouritegallery.php", map);
                    notifyDataSetChanged();
                    tvNoData.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                }else {
                    if (gallery.getIsFav().equals("true")){
                        addOrRemove("gallery/removefavouritegallery.php", map);
                        gallery.setIsFav("false");
                    }else {
                        map.put("created", fm.format(new Date()));
                        addOrRemove("gallery/addfavouritegaallery.php", map);
                        gallery.setIsFav("true");
                    }
                    notifyItemChanged(position);
                }
            });
        }else {
            new Handler().postDelayed(() -> holder.bind.itemVideo.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (holder.bind.itemVideo.getMeasuredWidth() / 1.38))), 100);
            holder.bind.cbFavVideo.setChecked(gallery.getIsFav().equals("true"));
            Glide.with(context).asBitmap().load(gallery.getImg()).centerCrop().into(holder.bind.ivVideo);
            holder.bind.itemVideo.setOnClickListener(v -> v.getContext().startActivity(new Intent(v.getContext(), VideoPlayer.class).putExtra("url", gallery.getUrl())));
            holder.bind.cbFavVideo.setOnClickListener(v -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("user_id", sessionManager.getCurrentlyLoggedUserId());
                map.put("video", gallery.getId());
                if (isFav){
                    list.remove(position);
                    addOrRemove("gallery/removefavouritevideo.php", map);
                    notifyDataSetChanged();
                    tvNoData.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                }else {
                    if (gallery.getIsFav().equals("true")){
                        addOrRemove("gallery/removefavouritevideo.php", map);
                        gallery.setIsFav("false");
                    }else {
                        map.put("created", fm.format(new Date()));
                        addOrRemove("gallery/addfavouritevideo.php", map);
                        gallery.setIsFav("true");
                    }
                    notifyItemChanged(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        ItemVideoBinding bind;

        public VH(@NonNull ItemVideoBinding itemVideoBinding) {
            super(itemVideoBinding.getRoot());
            this.bind = itemVideoBinding;
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
                        Toast.makeText(context, jsonObject.optString("data"), Toast.LENGTH_SHORT).show();
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

    // popup select image
    public void popupSelectImg(int pos) {
        crPos = pos;
        Dialog dialog = new Dialog(context);
        PopupImgOptionBinding imgOptionBinding = PopupImgOptionBinding.inflate(((Activity) context).getLayoutInflater());
        dialog.setContentView(imgOptionBinding.getRoot());
        AppUtil.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT);

        imgOptionBinding.btClose.setOnClickListener(v -> dialog.dismiss());
        new Handler().postDelayed(() -> {
            imgOptionBinding.iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (imgOptionBinding.iv.getMeasuredWidth() / 1.38)));
            Glide.with(context).load(list.get(pos).getImg()).into(imgOptionBinding.iv);
        }, 100);
        if (crPos == 0) {
            imgOptionBinding.btPre.setEnabled(false);
            imgOptionBinding.btPre.setVisibility(View.INVISIBLE);
        }
        if (crPos == list.size() - 1) {
            imgOptionBinding.btNext.setEnabled(false);
            imgOptionBinding.btNext.setVisibility(View.INVISIBLE);
        }
        imgOptionBinding.ivSave.setImageResource(list.get(pos).getIsFav().equals("true") ? R.mipmap.ic_heart_fill : R.mipmap.ic_heart);
        imgOptionBinding.btSave.setOnClickListener(v -> {
            HashMap<String, Object> map = new HashMap<>();
            map.put("user_id", sessionManager.getCurrentlyLoggedUserId());
            map.put("imageid", list.get(pos).getId());
            if (isFav){
                list.remove(pos);
                addOrRemove("gallery/removefavouritegallery.php", map);
                notifyDataSetChanged();
                tvNoData.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                dialog.dismiss();
            }else {
                if (list.get(pos).getIsFav().equals("true")) {
                    list.get(pos).setIsFav("false");
                    imgOptionBinding.ivSave.setImageResource(R.mipmap.ic_heart);
                    addOrRemove("gallery/removefavouritegallery.php", map);
                } else {
                    list.get(pos).setIsFav("true");
                    imgOptionBinding.ivSave.setImageResource(R.mipmap.ic_heart_fill);
                    addOrRemove("gallery/addfavouritegaallery.php", map);
                }
                notifyItemChanged(pos);
            }
        });
        imgOptionBinding.btPre.setOnClickListener(v -> {
            if (crPos != 0) {
                Glide.with(context).load(list.get(crPos - 1).getImg()).into(imgOptionBinding.iv);
                crPos--;
                imgOptionBinding.btNext.setEnabled(true);
                imgOptionBinding.btNext.setVisibility(View.VISIBLE);
                if (crPos == 0) {
                    imgOptionBinding.btPre.setEnabled(false);
                    imgOptionBinding.btPre.setVisibility(View.INVISIBLE);
                }
            }
        });
        imgOptionBinding.btNext.setOnClickListener(v -> {
            if (crPos < list.size()) {
                Glide.with(context).load(list.get(crPos + 1).getImg()).into(imgOptionBinding.iv);
                crPos++;
                imgOptionBinding.btPre.setEnabled(true);
                imgOptionBinding.btPre.setVisibility(View.VISIBLE);
                if (crPos == list.size() - 1) {
                    imgOptionBinding.btNext.setEnabled(false);
                    imgOptionBinding.btNext.setVisibility(View.INVISIBLE);
                }
            }
        });
        imgOptionBinding.btShare.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing Image");
            i.putExtra(Intent.EXTRA_TEXT, list.get(crPos).getImg());
            context.startActivity(Intent.createChooser(i, "Share Image"));
        });
        imgOptionBinding.btDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 917);
                } else {
                    AppUtil.saveImgToGallery(imgOptionBinding.iv, v.getContext());
                    Toast.makeText(context, context.getString(R.string.saved_to_the_gallery), Toast.LENGTH_SHORT).show();
                }
            } else {
                AppUtil.saveImgToGallery(imgOptionBinding.iv, v.getContext());
                Toast.makeText(context, context.getString(R.string.saved_to_the_gallery), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
