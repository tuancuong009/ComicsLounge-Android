package com.comics.lounge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.fragments.ConfirmTicketFragment;
import com.comics.lounge.modals.BookingHistory;
import com.comics.lounge.modals.ConfirmTicket;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConfirmTicketListItemAdpter extends RecyclerView.Adapter<ConfirmTicketListItemAdpter.ItemHolder> {
    private Context context = null;
    private LayoutInflater layoutInflater = null;
    private List<ConfirmTicket> bookingList = null;
    private ConfirmTicketFragment confirmTicketFragment = null;

    public ConfirmTicketListItemAdpter(Context context, List<ConfirmTicket> bookingList, ConfirmTicketFragment confirmTicketFragment) {
        this.context = context;
        this.bookingList = bookingList;
        this.confirmTicketFragment = confirmTicketFragment;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_confirm_ticket, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
        holder.setBookingData(bookingList.get(position).getBookingHistoryList().get(0));
        holder.renderNavMenuName();

        /*if (position % 2 == 1){
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(context,R.color.white));
        }else{
            holder.mainlayout.setBackgroundColor(ContextCompat.getColor(context,R.color.contact_us_edt_back_color));
        }*/

        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmTicketFragment.switchToConfirmTicketDetailFragment(bookingList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainlayout;
        private final AppCompatTextView dateTxt;
        private final AppCompatImageView backgroundImg;
        private final AppCompatTextView ticketNumTxt;
        private final AppCompatTextView ticketNameTXt;
        private BookingHistory bookingHistory;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.main_layout);
            backgroundImg = itemView.findViewById(R.id.background_image);
            ticketNumTxt = itemView.findViewById(R.id.event_id_txt);
            dateTxt = itemView.findViewById(R.id.event_date_txt);
            ticketNameTXt = itemView.findViewById(R.id.event_name_txt);
        }


        public void renderNavMenuName() {
            ticketNumTxt.setText("#" + bookingHistory.getVirtuemartOrderId());
            dateTxt.setText(bookingHistory.getEventDate() + " - " + bookingHistory.getShowTime());
            ticketNameTXt.setText(bookingHistory.getProductName());
            Picasso.get()
                    .load(bookingHistory.getImage())
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_foreground)
                    .into(backgroundImg);
        }


        public void setBookingData(BookingHistory bookingHistory) {
            this.bookingHistory = bookingHistory;
        }
    }
}
