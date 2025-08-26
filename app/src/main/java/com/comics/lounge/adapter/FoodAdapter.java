package com.comics.lounge.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comics.lounge.R;
import com.comics.lounge.databinding.ItemFoodBinding;
import com.comics.lounge.databinding.PopupFoodOptionBinding;
import com.comics.lounge.modals.MenuItem;
import com.comics.lounge.utils.AppUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.VH> {
    Context context;
    List<MenuItem> list;

    public FoodAdapter(Context context, List<MenuItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MenuItem menuItem = list.get(position);
        holder.bind.tvName.setText(menuItem.getName());
        holder.bind.tvCost.setText(menuItem.getPrice());
        holder.bind.tvDes.setText(Html.fromHtml(menuItem.getDes(), Html.FROM_HTML_MODE_COMPACT));
        Glide.with(context).asBitmap().load(menuItem.getImg()).error(R.mipmap.ic_no_img).centerCrop().into(holder.bind.iv);
        holder.itemView.setOnClickListener(v -> popupFoodOpt(menuItem));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemFoodBinding bind;
        public VH(@NonNull ItemFoodBinding itemFoodBinding) {
            super(itemFoodBinding.getRoot());
            this.bind = itemFoodBinding;
        }
    }

    // popup food option
    private void popupFoodOpt(MenuItem item){
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme2);
        PopupFoodOptionBinding foodOptionBinding = PopupFoodOptionBinding.inflate(((Activity) context).getLayoutInflater());
        dialog.setContentView(foodOptionBinding.getRoot());
        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        foodOptionBinding.tvName.setText(item.getName());
        foodOptionBinding.tvDes.setText(Html.fromHtml(item.getDes(), Html.FROM_HTML_MODE_COMPACT));
        Glide.with(context).load(item.getPopupImg()).error(R.mipmap.ic_no_img).centerCrop().into(foodOptionBinding.ivFood);
        foodOptionBinding.ivFood.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(context).widthPixels / 1.23)));
        foodOptionBinding.btClose.setOnClickListener(v -> dialog.dismiss());
        foodOptionBinding.btShare.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing Image");
            i.putExtra(Intent.EXTRA_TEXT, item.getImg());
            context.startActivity(Intent.createChooser(i, "Share Image"));
        });
        foodOptionBinding.btDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 917);
                } else {
                    AppUtil.saveImgToGallery(foodOptionBinding.ivFood, v.getContext());
                    Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }
            } else {
                AppUtil.saveImgToGallery(foodOptionBinding.ivFood, v.getContext());
                Toast.makeText(context, context.getString(R.string.saved), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
