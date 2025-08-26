package com.comics.lounge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.fragments.FrmEventDetail;

import java.util.List;

public class EventTimeListItemAdpter extends RecyclerView.Adapter<EventTimeListItemAdpter.ItemHolder> {
    private Context context = null;
    private LayoutInflater layoutInflater = null;
    private List<String> sessionList = null;
    private FrmEventDetail frmEventDetail = null;
    private int selectPosition = -1;


    public EventTimeListItemAdpter(Context applicationContext, List<String> sessionList, FrmEventDetail frmEventDetail) {
        this.context = applicationContext;
        this.sessionList = sessionList;
        this.frmEventDetail = frmEventDetail;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.event_date_item_layout, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
        holder.setEventDate(sessionList.get(position));
        holder.renderNavMenuName();
        holder.mainlayout.setOnClickListener(v -> {
            frmEventDetail.clickTimeForEvent(sessionList.get(position),position);
            selectPosition = position;
            notifyDataSetChanged();
        });

        if (selectPosition == position) {
            holder.mainlayout.setBackgroundResource(R.drawable.event_date_select_border_drawble);
            holder.dateTxt.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            holder.mainlayout.setBackgroundResource(R.drawable.event_date_border_drawble);
            holder.dateTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }



    public class ItemHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainlayout;
        private final AppCompatTextView dateTxt;
        private String timeStr;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.main_layout);
            dateTxt = itemView.findViewById(R.id.date_txt);
        }

        public void renderNavMenuName() {
            dateTxt.setText(timeStr);
        }

        public void setEventDate(String dateStr) {
            this.timeStr = dateStr;
        }
    }
}
