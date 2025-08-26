package com.comics.lounge.adapter;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.databinding.ItemDesMbShipBinding;

import java.util.List;

public class DesMbShipAdapter extends RecyclerView.Adapter<DesMbShipAdapter.VH> {
    List<String> list;

    public DesMbShipAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDesMbShipBinding binding = ItemDesMbShipBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind.tvDes.setText(Html.fromHtml(list.get(position).replace("\r", "").replace("\n", ""), Html.FROM_HTML_MODE_COMPACT));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemDesMbShipBinding bind;
        public VH(@NonNull ItemDesMbShipBinding itemDesMbShipBinding) {
            super(itemDesMbShipBinding.getRoot());
            this.bind = itemDesMbShipBinding;
        }
    }
}
