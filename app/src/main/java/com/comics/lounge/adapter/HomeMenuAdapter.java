package com.comics.lounge.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comics.lounge.R;
import com.comics.lounge.activity.BuyMembership;
import com.comics.lounge.activity.MyMemberShip;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.ItemMenuHomeBinding;
import com.comics.lounge.modals.HomeMenu;

import java.util.List;

import io.intercom.android.sdk.Intercom;

public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.VH> {
    List<HomeMenu> list;
    Context context;

    public HomeMenuAdapter(List<HomeMenu> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuHomeBinding binding = ItemMenuHomeBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        HomeMenu hm = list.get(position);
        holder.bind.tvName.setText(hm.getName());
        Glide.with(context).load(hm.getImg()).centerCrop().into(holder.bind.ivImg);
        new Handler().postDelayed(() -> holder.bind.cv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (holder.bind.cv.getMeasuredWidth()/1.2))), 100);
        holder.itemView.setOnClickListener(v -> {
            holder.bind.cv.setCardBackgroundColor(context.getColor(R.color.bg_splash));
            holder.bind.tvName.setTextColor(context.getColor(R.color.white));
            holder.bind.ivImg.setColorFilter(context.getColor(R.color.white));
            new Handler().postDelayed(() -> {
                holder.bind.cv.setCardBackgroundColor(context.getColor(R.color.white));
                holder.bind.tvName.setTextColor(context.getColor(R.color.bg_splash));
                holder.bind.ivImg.setColorFilter(context.getColor(R.color.bg_splash));
            }, 10);
            Intent it;
            switch (position){
                case 0:
                    it = new Intent(context, NewMain.class);
                    it.putExtra("screen", "calendar");
                    context.startActivity(it);
                    break;
                case 1:
                    it = new Intent(context, NewMain.class);
                    it.putExtra("screen", "menu");
                    context.startActivity(it);
                    break;
                case 2:
                    it = new Intent(context, NewMain.class);
                    it.putExtra("screen", "account");
                    context.startActivity(it);
                    break;
                case 3:
                    it = new Intent(context, NewMain.class);
                    if (hm.getName().equals(context.getString(R.string.my_membership_str))){
                        it.putExtra("screen", "membership");
                    }else {
                        it.putExtra("screen", "become_membership");
                    }
                    context.startActivity(it);
                    break;
                case 4:
                    it = new Intent(context, NewMain.class);
                    it.putExtra("screen", "gallery");
                    context.startActivity(it);
                    break;
                case 5:
                    it = new Intent(context, NewMain.class);
                    it.putExtra("screen", "offers");
                    context.startActivity(it);
                    break;
                case 6:
                    it = new Intent(context, NewMain.class);
                    it.putExtra("screen", "contact");
                    context.startActivity(it);
                    break;
                case 7:
                    it = new Intent(context, NewMain.class);
                    it.putExtra("screen", "concierge");
                    context.startActivity(it);
                    break;
            }
        });
        if (position == 7){
            Intercom.client().addUnreadConversationCountListener(i -> {
                if (i > 0){
                    holder.bind.cvUnread.setVisibility(View.VISIBLE);
                    holder.bind.tvUnread.setText(String.valueOf(Intercom.client().getUnreadConversationCount()));
                }else {
                    holder.bind.cvUnread.setVisibility(View.GONE);
                }
            });
        }else {
            holder.bind.cvUnread.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemMenuHomeBinding bind;
        public VH(@NonNull ItemMenuHomeBinding itemMenuHomeBinding) {
            super(itemMenuHomeBinding.getRoot());
            this.bind = itemMenuHomeBinding;
        }
    }
}
