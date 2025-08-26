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
import com.comics.lounge.fragments.BookingHistoryFragment;
import com.comics.lounge.modals.BookingHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookingListItemAdpter extends RecyclerView.Adapter<BookingListItemAdpter.ItemHolder> {
    private Context context = null;
    private LayoutInflater layoutInflater = null;
    private List<BookingHistory> bookingList = null;
    private SimpleDateFormat inputFormat = null;
    private SimpleDateFormat outputFormat = null;
    private Date date = null;

    public BookingListItemAdpter(Context context, List<BookingHistory> bookingList, BookingHistoryFragment bookingHistoryFragment) {
        this.context = context;
        this.bookingList = bookingList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        date = new Date();
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.booking_history_item_cell, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.setBookingData(bookingList.get(position));
        holder.renderNavMenuName();

        if (position % 2 == 1) {
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(context, R.color.contact_us_edt_back_color));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void attachFormate(SimpleDateFormat inputFormat, SimpleDateFormat outputFormat) {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {


        private final LinearLayout mainlayout;
        private final AppCompatTextView titleTxt;
        private final AppCompatTextView dateTxt;
        private final AppCompatTextView orderIdTxt;
        private final AppCompatTextView statusTxt;
        private BookingHistory bookingHistory;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.main_layout);
            titleTxt = itemView.findViewById(R.id.bh_title_txt);
            dateTxt = itemView.findViewById(R.id.bh_date_txt);
            orderIdTxt = itemView.findViewById(R.id.bh_orderid_txt);
            statusTxt = itemView.findViewById(R.id.bh_status_txt);
        }


        public void renderNavMenuName() {
            titleTxt.setText(bookingHistory.getProductName());
            try {
                date = inputFormat.parse(bookingHistory.getEventDate());
                dateTxt.setText(outputFormat.format(date));
                dateTxt.setText("Date: " + outputFormat.format(date) + " - " + bookingHistory.getShowTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            orderIdTxt.setText("Order ID: #" + bookingHistory.getVirtuemartOrderId());
            statusTxt.setText("Status : " + bookingHistory.getProductStatus());
        }


        public void setBookingData(BookingHistory bookingHistory) {
            this.bookingHistory = bookingHistory;
        }
    }
}
