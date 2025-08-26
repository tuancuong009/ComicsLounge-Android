package com.comics.lounge.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comics.lounge.databinding.ItemSpecialOfferBinding;
import com.comics.lounge.databinding.PopupViewOfferBinding;
import com.comics.lounge.modals.Offers;
import com.comics.lounge.modals.QA;
import com.comics.lounge.utils.AppUtil;

import java.util.List;

public class SpecialOfferAdapter extends RecyclerView.Adapter<SpecialOfferAdapter.VH> {
    Context context;
    List<Offers> list;

    public SpecialOfferAdapter(Context context, List<Offers> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSpecialOfferBinding binding = ItemSpecialOfferBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Offers offers = list.get(position);
        holder.bind.tvDes.setText(offers.getDes());
        Glide.with(context).load(offers.getImg()).into(holder.bind.iv);
        holder.bind.cv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(context).widthPixels / 2.02)));
        holder.itemView.setOnClickListener(v -> popupViewOffer(offers.getDes(), offers.getImg()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemSpecialOfferBinding bind;
        public VH(@NonNull ItemSpecialOfferBinding itemSpecialOfferBinding) {
            super(itemSpecialOfferBinding.getRoot());
            this.bind = itemSpecialOfferBinding;
        }
    }

    // popup view offer
    private void popupViewOffer(String title, String img){
        Dialog dialog = new Dialog(context);
        PopupViewOfferBinding viewOfferBinding = PopupViewOfferBinding.inflate(((Activity) context).getLayoutInflater());
        dialog.setContentView(viewOfferBinding.getRoot());
        AppUtil.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);

        viewOfferBinding.btClose.setOnClickListener(v -> dialog.dismiss());
        viewOfferBinding.tvName.setText(title);
        Glide.with(context).load(img).into(viewOfferBinding.iv);

        dialog.show();
    }
}
