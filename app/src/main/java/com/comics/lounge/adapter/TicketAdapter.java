package com.comics.lounge.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.activity.ConfirmTicketDetailActivity;
import com.comics.lounge.activity.MainActivity;
import com.comics.lounge.databinding.ItemTicketBinding;
import com.comics.lounge.modals.TicketHistory;
import com.comics.lounge.utils.DatesUtils;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.VH> {
    String type;
    Context context;
    List<TicketHistory> list;

    public TicketAdapter(String type, Context context, List<TicketHistory> list) {
        this.type = type;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketBinding binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TicketHistory th = list.get(position);
        holder.bind.tvName.setText(th.getName());
        holder.bind.tvDate.setText(DatesUtils.fmDate2(th.getOrderDate()) +" "+th.getOrderTime());
        holder.bind.tvTicket.setText(String.valueOf(th.getShowCount()));
        holder.bind.tvMeal.setText(String.valueOf(th.getMealCount()));
        if (type.equals("history")){
            holder.bind.tvName.setTextColor(context.getColor(R.color.grey_3));
            holder.bind.tvDate.setTextColor(context.getColor(R.color.grey_3));
            holder.bind.tvTicket.setTextColor(context.getColor(R.color.grey_3));
            holder.bind.tvMeal.setTextColor(context.getColor(R.color.grey_3));
            holder.bind.ivTicket.setColorFilter(context.getColor(R.color.grey_3));
            holder.bind.ivMeal.setColorFilter(context.getColor(R.color.grey_3));
        }else {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ConfirmTicketDetailActivity.class);
                intent.putExtra("orderID", Integer.parseInt(th.getOrderId()));
                intent.putExtra("orderDate", th.getOrderDate());
                intent.putExtra("orderTime", th.getOrderTime());
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemTicketBinding bind;
        public VH(@NonNull ItemTicketBinding itemTicketBinding) {
            super(itemTicketBinding.getRoot());
            this.bind = itemTicketBinding;
        }
    }
}
