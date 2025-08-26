package com.comics.lounge.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.fragments.HomeFragment;
import com.comics.lounge.modals.Event;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeEventListItemAdpter extends RecyclerView.Adapter<HomeEventListItemAdpter.ItemHolder> {
    private HomeFragment homeFragment;
    private List<Event> eventList = null;
    private Context context;
    private LayoutInflater layoutInflater;


    public HomeEventListItemAdpter(Context context, List<Event> itemList, HomeFragment homeFragment) {
        this.context = context;
        this.eventList = itemList;
        this.homeFragment = homeFragment;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_event, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
        holder.setEventData(eventList.get(position));
        holder.renderEventName();

            Log.w("KRUTI", "onBindViewHolder: event name ::"+eventList.get(position).getImage() + "    name ::" + eventList.get(position).getProductName() );



        holder.getTicketBtn.setOnClickListener(v -> homeFragment.switchToEventDetailFragment(eventList.get(position)));

        holder.cardViewLayout.setOnClickListener(v -> homeFragment.switchToEventDetailFragment(eventList.get(position)));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final CardView cardViewLayout;
        private final AppCompatImageView eventImage;
        private final AppCompatTextView eventDesc;
        private final AppCompatTextView eventDate;
        private final MaterialButton getTicketBtn;
        private AppCompatTextView eventName;
        private Event event;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name_txt);
            eventImage = itemView.findViewById(R.id.event_imageview);
            eventDesc = itemView.findViewById(R.id.product_desc_txt);
            eventDate = itemView.findViewById(R.id.event_date_txt);
            getTicketBtn = itemView.findViewById(R.id.get_ticket_btn);
            cardViewLayout = itemView.findViewById(R.id.card_view_layout);
            eventImage.setClipToOutline(true);
        }

        public void setEventData(Event event) {
            this.event = event;
        }

        public void renderEventName() {
            eventName.setText(event.getProductName());
            Picasso.get()
                    .load(event.getImage())
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_foreground)
                    .into(eventImage);
            eventDesc.setText(event.getProductDesc());
            eventDate.setText(event.getDate());
        }
    }
}
