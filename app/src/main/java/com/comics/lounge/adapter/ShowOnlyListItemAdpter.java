package com.comics.lounge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.activity.ConfirmTicketDetailActivity;
import com.comics.lounge.modals.BookingHistory;

import java.util.List;

import static com.comics.lounge.conf.Constant.STATUS_CANCELLED;

public class ShowOnlyListItemAdpter extends RecyclerView.Adapter<ShowOnlyListItemAdpter.ItemHolder> {

    private LayoutInflater layoutInflater = null;
    private List<BookingHistory> bookingList;
    private ConfirmTicketDetailActivity confirmTicketDetailActivity = null;

    public ShowOnlyListItemAdpter(Context context, List<BookingHistory> bookingList, ConfirmTicketDetailActivity confirmTicketDetailActivity) {
        this.bookingList = bookingList;
        this.confirmTicketDetailActivity = confirmTicketDetailActivity;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_ticket_show, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
        holder.setBookingData(bookingList.get(position));
        holder.renderNavMenuName();

        holder.canceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingHistory bookingHistory = bookingList.get(position);
                confirmTicketDetailActivity.cancelTicket(position, bookingHistory.getShowType().toLowerCase());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainlayout;
        private final LinearLayout canceLayout;
        private final AppCompatTextView productNameTxt;
        private final AppCompatTextView productTypeTxt;
        private final TextView tvCancelTitle;
        private BookingHistory bookingHistory;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.main_layout);
            productNameTxt = itemView.findViewById(R.id.product_name_txt);
            productTypeTxt = itemView.findViewById(R.id.product_type_txt);
            canceLayout = itemView.findViewById(R.id.cancel_layout);
            tvCancelTitle = itemView.findViewById(R.id.tvCancelTitle);
        }

        public void renderNavMenuName() {
            productNameTxt.setText(bookingHistory.getProductName());
            productTypeTxt.setText(bookingHistory.getProductType());

            if (bookingHistory.getProductStatus().equals(STATUS_CANCELLED)) {
                canceLayout.setVisibility(View.GONE);
                tvCancelTitle.setVisibility(View.VISIBLE);
            } else {
                canceLayout.setVisibility(View.VISIBLE);
                tvCancelTitle.setVisibility(View.GONE);
            }
        }

        public void setBookingData(BookingHistory bookingHistory) {
            this.bookingHistory = bookingHistory;
        }
    }
}
