package com.comics.lounge.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.ItemMbPlanBinding;
import com.comics.lounge.fragments.FrmBuyMb;
import com.comics.lounge.modals.Membership;

import java.util.List;

public class MbPlanAdapter extends RecyclerView.Adapter<MbPlanAdapter.VH> {
    List<Membership> list;
    NewMain context;

    public MbPlanAdapter(List<Membership> list, NewMain context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMbPlanBinding binding = ItemMbPlanBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Membership mb = list.get(position);
        holder.bind.tvName.setText(mb.getName());
        holder.bind.tvPrice.setText("$"+mb.getPrice());
        Glide.with(context).load(mb.getImage()).fitCenter().into(holder.bind.iv);
        holder.itemView.setOnClickListener(v -> {
            holder.bind.cv.setCardBackgroundColor(context.getColor(R.color.bg_splash));
            holder.bind.tvName.setTextColor(context.getColor(R.color.white));
            holder.bind.tvPrice.setTextColor(context.getColor(R.color.white));
            new Handler().postDelayed(() -> {
                holder.bind.cv.setCardBackgroundColor(context.getColor(R.color.white));
                holder.bind.tvName.setTextColor(context.getColor(R.color.black));
                holder.bind.tvPrice.setTextColor(context.getColor(R.color.bg_splash));
            }, 10);
            FrmBuyMb frm = new FrmBuyMb();
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", mb);
            frm.setArguments(bundle);
            context.addFrmDetail(frm);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemMbPlanBinding bind;
        public VH(@NonNull ItemMbPlanBinding itemMbPlanBinding) {
            super(itemMbPlanBinding.getRoot());
            this.bind = itemMbPlanBinding;
        }
    }
}
