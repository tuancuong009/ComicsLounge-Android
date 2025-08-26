package com.comics.lounge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.databinding.ItemDateBinding;
import com.comics.lounge.modals.PriceDates;
import com.comics.lounge.servicecallback.ClickItemEventDate;
import com.comics.lounge.utils.DatesUtils;

import java.util.ArrayList;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.VH> {
    ArrayList<PriceDates> list;
    Context context;
    ClickItemEventDate clickItemEventDate;
    int selectedPosition = -1;

    public DatesAdapter(ArrayList<PriceDates> list, Context context, ClickItemEventDate clickItemEventDate) {
        this.list = list;
        this.context = context;
        this.clickItemEventDate = clickItemEventDate;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDateBinding binding = ItemDateBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PriceDates priceDates = list.get(position);
        holder.bind.tvDate.setText(DatesUtils.fmDate(priceDates.getDate()));
        if (selectedPosition == position) {
            holder.itemView.setSelected(true);
            holder.bind.cvItem.setCardBackgroundColor(context.getColor(R.color.bg_splash));
            holder.bind.tvDate.setTextColor(context.getColor(R.color.white));
            holder.bind.cvItem.setForeground(null);
        } else {
            holder.itemView.setSelected(false);
            holder.bind.cvItem.setCardBackgroundColor(context.getColor(android.R.color.transparent));
            holder.bind.cvItem.setForeground(context.getDrawable(R.drawable.bg_border_gray));
            holder.bind.tvDate.setTextColor(context.getColor(R.color.gray_1));
        }
        holder.itemView.setOnClickListener(v -> {
            if (holder.itemView.isSelected()){
                holder.itemView.setSelected(false);
                holder.bind.cvItem.setCardBackgroundColor(context.getColor(android.R.color.transparent));
                holder.bind.cvItem.setForeground(context.getDrawable(R.drawable.bg_border_gray));
                holder.bind.tvDate.setTextColor(context.getColor(R.color.gray_1));
                clickItemEventDate.unSelectDate();
            }else{
                if (priceDates.getQty() > 0){
                    if (selectedPosition >= 0)
                        notifyItemChanged(selectedPosition);
                    selectedPosition = holder.getLayoutPosition();
                    notifyItemChanged(selectedPosition);
                    clickItemEventDate.selectDate(priceDates);
                }else {
                    Toast.makeText(context, R.string.free_ticket_over, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemDateBinding bind;
        public VH(@NonNull ItemDateBinding itemDateBinding) {
            super(itemDateBinding.getRoot());
            this.bind = itemDateBinding;
        }
    }
}
