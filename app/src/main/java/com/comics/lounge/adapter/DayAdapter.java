package com.comics.lounge.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.databinding.ItemDayBinding;
import com.comics.lounge.servicecallback.ClickItemCalendar;

import java.util.Calendar;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.VH> {
    int dayOfWeek, selectMonth, selectYear;
    Context context;
    ClickItemCalendar clickItemCalendar;
    int selectedPosition = -1;

    public DayAdapter(int dayOfWeek, int selectMonth, int selectYear, Context context, ClickItemCalendar clickItemCalendar) {
        this.dayOfWeek = dayOfWeek;
        this.selectMonth = selectMonth;
        this.selectYear = selectYear;
        this.context = context;
        this.clickItemCalendar = clickItemCalendar;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDayBinding binding = ItemDayBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Calendar c = Calendar.getInstance();
        int p = position + 1 - dayOfWeek;
        if (p > 0){
            if (p > getMonthDays(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))){
                holder.bind.tvDay.setText(String.valueOf(p - getMonthDays(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
                holder.bind.tvDay.setTextColor(context.getColor(R.color.grey_2));
            }else {
                if (c.get(Calendar.YEAR) == selectYear && c.get(Calendar.MONTH) == selectMonth && c.get(Calendar.DAY_OF_MONTH) == p){
                    holder.bind.tvDay.setTextColor(context.getColor(R.color.bg_splash));
                }else {
                    holder.bind.tvDay.setTextColor(context.getColor(R.color.black));
                }
                holder.bind.tvDay.setText(String.valueOf(p));
                if (selectedPosition == position) {
                    holder.itemView.setSelected(true);
                    holder.bind.cvDay.setCardBackgroundColor(context.getColor(R.color.bg_splash));
                    holder.bind.tvDay.setTextColor(context.getColor(R.color.white));
                } else {
                    holder.itemView.setSelected(false);
                    holder.bind.cvDay.setCardBackgroundColor(context.getColor(android.R.color.transparent));
                    if (c.get(Calendar.YEAR) == selectYear && c.get(Calendar.MONTH) == selectMonth && c.get(Calendar.DAY_OF_MONTH) == p){
                        holder.bind.tvDay.setTextColor(context.getColor(R.color.bg_splash));
                    }else {
                        holder.bind.tvDay.setTextColor(context.getColor(R.color.black));
                    }
                }
                holder.itemView.setOnClickListener(v -> {
                    if (holder.itemView.isSelected()){
                        holder.itemView.setSelected(false);
                        holder.bind.cvDay.setCardBackgroundColor(context.getColor(android.R.color.transparent));
                        if (c.get(Calendar.YEAR) == selectYear && c.get(Calendar.MONTH) == selectMonth && c.get(Calendar.DAY_OF_MONTH) == p){
                            holder.bind.tvDay.setTextColor(context.getColor(R.color.bg_splash));
                        }else {
                            holder.bind.tvDay.setTextColor(context.getColor(R.color.black));
                        }
                        clickItemCalendar.unSelectDay();
                    }else{
                        if (selectedPosition >= 0)
                            notifyItemChanged(selectedPosition);
                        selectedPosition = holder.getLayoutPosition();
                        notifyItemChanged(selectedPosition);
                        clickItemCalendar.selectedDay(p+"-"+(selectMonth + 1)+"-"+selectYear);
                    }
                });
            }

        }else {
            holder.bind.tvDay.setText(String.valueOf(getMonthDays(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)) + p));
            holder.bind.tvDay.setTextColor(context.getColor(R.color.grey_2));
        }
    }

    @Override
    public int getItemCount() {
        return 35;
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemDayBinding bind;
        public VH(@NonNull ItemDayBinding itemDayBinding) {
            super(itemDayBinding.getRoot());
            this.bind = itemDayBinding;
        }
    }

    public int getMonthDays(int month, int year) {
        int daysInMonth;
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        } else {
            if (month == 2) {
                daysInMonth = (year % 4 == 0) ? 29 : 28;
            } else {
                daysInMonth = 31;
            }
        }
        return daysInMonth;
    }
}
