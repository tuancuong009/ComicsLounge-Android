package com.comics.lounge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.fragments.FrmEventDetail;
import com.comics.lounge.utils.DatesUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class EventDateListItemAdpter extends RecyclerView.Adapter<EventDateListItemAdpter.ItemHolder> {
    private Context context = null;
    private LayoutInflater layoutInflater = null;
    private List<String> eventPriceDatesList = null;
    private FrmEventDetail frmEventDetail = null;
    private SimpleDateFormat inputFormat = null;
    private SimpleDateFormat outputFormat = null;
    private Date date = null;
    private int selectPosition = -1;


    public EventDateListItemAdpter(Context context, Set<String> eventPrice, FrmEventDetail frmEventDetail) {
        this.context = context;
        this.eventPriceDatesList = new LinkedList<String>(eventPrice);
        Collections.sort(eventPriceDatesList, new Comparator<String>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        this.frmEventDetail = frmEventDetail;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        date = new Date();

    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.event_date_item_layout, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
        holder.setEventDate(eventPriceDatesList.get(position));
        holder.dateTxt.setText(DatesUtils.fmDate(eventPriceDatesList.get(position)));
        holder.mainlayout.setOnClickListener(v -> {
            if (selectPosition >= 0)
                notifyItemChanged(selectPosition);
            selectPosition = holder.getLayoutPosition();
            notifyItemChanged(selectPosition);
            frmEventDetail.clickDateView(eventPriceDatesList.get(position));
        });
        if (selectPosition == position) {
            holder.mainlayout.setCardBackgroundColor(context.getColor(R.color.bg_splash));
            holder.mainlayout.setForeground(null);
            holder.dateTxt.setTextColor(context.getColor(R.color.white));
        } else {
            holder.mainlayout.setCardBackgroundColor(context.getColor(android.R.color.transparent));
            holder.mainlayout.setForeground(context.getDrawable(R.drawable.bg_border_gray));
            holder.dateTxt.setTextColor(context.getColor(R.color.gray_1));
        }
    }

    @Override
    public int getItemCount() {
        return eventPriceDatesList.size();
    }

    public void addDateFormate(SimpleDateFormat inputFormat, SimpleDateFormat outputFormat) {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final CardView mainlayout;
        private final AppCompatTextView dateTxt;
        private String dateStr;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.main_layout);
            dateTxt = itemView.findViewById(R.id.date_txt);
        }

        public void setEventDate(String dateStr) {
            System.out.println("Current_date ::" + dateStr);
            this.dateStr = dateStr;
        }
    }
}
