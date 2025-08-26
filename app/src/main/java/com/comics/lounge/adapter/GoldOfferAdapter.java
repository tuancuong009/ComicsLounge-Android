package com.comics.lounge.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.comics.lounge.R;
import com.comics.lounge.databinding.ItemGoldOfferBinding;
import com.comics.lounge.databinding.ItemSpecialOfferBinding;
import com.comics.lounge.databinding.PopupViewOfferBinding;
import com.comics.lounge.modals.Offers;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;

import java.util.List;

public class GoldOfferAdapter extends RecyclerView.Adapter<GoldOfferAdapter.VH> {
    Context context;
    List<Offers> list;
    SessionManager sessionManager;

    public GoldOfferAdapter(Context context, List<Offers> list, SessionManager sessionManager) {
        this.context = context;
        this.list = list;
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoldOfferBinding binding = ItemGoldOfferBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Offers offers = list.get(position);
        holder.bind.tvDes.setText(offers.getDes());
        Glide.with(context).load(offers.getImg()).into(holder.bind.iv);
        holder.bind.cv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(context).widthPixels / 1.55)));
        if (sessionManager.getCurrentUser().getMembership()){
            holder.itemView.setOnClickListener(v -> popupViewOffer(offers.getDes(), offers.getImg(), position));
        }else {
            holder.bind.ivLock.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        ItemGoldOfferBinding bind;
        public VH(@NonNull ItemGoldOfferBinding itemGoldOfferBinding) {
            super(itemGoldOfferBinding.getRoot());
            this.bind = itemGoldOfferBinding;
        }
    }

    // popup view offer
    private void popupViewOffer(String title, String img, int pos){
        Dialog dialog = new Dialog(context);
        PopupViewOfferBinding viewOfferBinding = PopupViewOfferBinding.inflate(((Activity) context).getLayoutInflater());
        dialog.setContentView(viewOfferBinding.getRoot());
        AppUtil.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.WRAP_CONTENT);

        viewOfferBinding.btClose.setOnClickListener(v -> dialog.dismiss());

        Glide.with(context).load(img).into(viewOfferBinding.iv);
        viewOfferBinding.btInviteFr.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing Image");
            i.putExtra(Intent.EXTRA_TEXT, img);
            context.startActivity(Intent.createChooser(i, "Share Image"));
        });
        if (pos == 0){
            viewOfferBinding.iv.setImageResource(R.drawable.img_voucher);
            viewOfferBinding.tvName.setText(context.getString(R.string.members_only_special));
//            viewOfferBinding.tvInfo.setText(context.getString(R.string.invite_a_friend_and_receive_a_drink_voucher_for_20));
            viewOfferBinding.btInviteFr.setVisibility(View.VISIBLE);
            Shader shader = new LinearGradient(0,0,0,viewOfferBinding.tvName.getLineHeight(),
                    context.getColor(R.color.yellow), context.getColor(R.color.yellow1), Shader.TileMode.REPEAT);
            viewOfferBinding.tvName.getPaint().setShader(shader);
        }else {
            viewOfferBinding.tvName.setVisibility(View.GONE);
            viewOfferBinding.tvInfo.setText(title);
        }

        dialog.show();
    }
}
