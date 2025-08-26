package com.comics.lounge.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.adapter.pager.CalenderDatePagerAdpter;
import com.comics.lounge.modals.Event;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CalenderDateListItemAdpter extends RecyclerView.Adapter<CalenderDateListItemAdpter.ItemHolder> {
    private final CalenderDatePagerAdpter calenderDatePagerAdpter;
    private List<Event> calenderModals;
    private Context context = null;
    private LayoutInflater layoutInflater = null;
    private SimpleDateFormat inputFormat = null;
    private SimpleDateFormat outputFormat = null;
    private Date date = null;


    public CalenderDateListItemAdpter(Context context, List<Event> calenderModals,
                                      CalenderDatePagerAdpter calenderDatePagerAdpter, SimpleDateFormat inputFormate, SimpleDateFormat outputFormate) {
        this.context = context;
        this.calenderModals = calenderModals;
        this.calenderDatePagerAdpter = calenderDatePagerAdpter;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        date = new Date();
        this.inputFormat = inputFormate;
        this.outputFormat = outputFormate;
        Log.w("KB", "CalenderDateListItemAdpter: "+new Gson().toJson(calenderModals));
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.calender_date_list_item_cell_layout, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, final int position) {
        holder.setEventObj(calenderModals.get(position));
        holder.renderNavMenuName();
        holder.mainlayout.setOnClickListener(v -> {
            calenderDatePagerAdpter.clickDateView(calenderModals.get(position));
            notifyDataSetChanged();
        });

        if (position % 2 == 1) {
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.contact_us_edt_back_color));
        }
    }

    @Override
    public int getItemCount() {
        return calenderModals.size();
    }

    public void addListData(List<Event> filterEventsList) {
        calenderModals.clear();
        calenderModals.addAll(filterEventsList);
        //calenderModals = filterEventsList;
        notifyDataSetChanged();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainlayout;
        private final AppCompatTextView dateTxt;
        private final AppCompatTextView dateNoTxt;
        private final AppCompatTextView eventTitle;
        private Event calenderModal;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.main_layout);
            dateTxt = itemView.findViewById(R.id.date_txt);
            dateNoTxt = itemView.findViewById(R.id.date_no_str);
            eventTitle = itemView.findViewById(R.id.event_title);
        }

        public void renderNavMenuName() {
            try {

                if(calenderModal.getSplitedEventStartDate() ==null || calenderModal.getSplitedEventStartDate().isEmpty()){

                    date = inputFormat.parse(calenderModal.getStartDate());
                }else{
                    date = inputFormat.parse(calenderModal.getSplitedEventStartDate());
                }
            //    date = inputFormat.parse(calenderModal.getSplitedEventStartDate());
                dateNoTxt.setText(outputFormat.format(date) + "th");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            eventTitle.setText(calenderModal.getProductName());
            dateTxt.setText(calenderModal.getShowTime());
        }


        public void setEventObj(Event calenderModal) {
            this.calenderModal = calenderModal;
        }
    }
}
