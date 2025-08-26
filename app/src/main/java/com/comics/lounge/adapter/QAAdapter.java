package com.comics.lounge.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.databinding.ItemQaBinding;
import com.comics.lounge.modals.MenuItem;
import com.comics.lounge.modals.QA;
import com.comics.lounge.utils.AppUtil;

import java.util.List;

public class QAAdapter extends RecyclerView.Adapter<QAAdapter.VH> {
    Context context;
    List<QA> list;

    public QAAdapter(Context context, List<QA> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQaBinding binding = ItemQaBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        QA qa = list.get(position);
        holder.bind.tvQues.setText(qa.getQues());
        holder.bind.tvAns.setText(qa.getAwns());
        holder.bind.rlQues.setOnClickListener(v -> {
            qa.setHidden(!qa.isHidden());
            notifyItemChanged(position);
        });
        if (qa.isHidden()){
            AppUtil.slideUp(holder.bind.tvAns);
        }else {
            AppUtil.slideDown(holder.bind.tvAns);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemQaBinding bind;
        public VH(@NonNull ItemQaBinding itemQaBinding) {
            super(itemQaBinding.getRoot());
            this.bind = itemQaBinding;
        }
    }
}
