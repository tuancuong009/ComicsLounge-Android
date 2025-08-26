package com.comics.lounge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.fragments.MembershipFragment;
import com.comics.lounge.modals.BookingHistory;
import com.comics.lounge.modals.Membership;
import com.comics.lounge.utils.NumberUtils;
import com.comics.lounge.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MembershipItemCellAdpter extends RecyclerView.Adapter<MembershipItemCellAdpter.ItemHolder> {

    private final Context context;
    private final List<Membership> memberships;
    private final String memberShipId;
    private MembershipFragment membershipFragment;
    private LayoutInflater layoutInflater = null;

    public MembershipItemCellAdpter(Context context, List<Membership> memberships, String memberShipId) {
        this.context = context;
        this.memberships = memberships;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.memberShipId = memberShipId;
    }


    @NonNull
    @Override
    public MembershipItemCellAdpter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.membership_item_cell_fragment, parent, false);
        return new MembershipItemCellAdpter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipItemCellAdpter.ItemHolder holder, int position) {
        holder.setMemberShip(memberships.get(position));
        holder.renderNavMenuName();
        holder.joinEventLayout.setVisibility(View.VISIBLE);
        holder.joinBtn.setOnClickListener(v -> {
            if (holder.isCheckAgrree.isChecked()) {
                membershipFragment.switchToPaymentScreen(memberships.get(position));
            } else {
                membershipFragment.displaySnackbarMessage(context.getString(R.string.pls_check_term_condition));
            }
        });
        holder.joinEventLayout.setVisibility(View.VISIBLE);
        /*if (String.valueOf(memberships.get(position).getId()).equals(memberShipId)) {
            holder.joinEventLayout.setVisibility(View.GONE);
        } else {
            holder.joinEventLayout.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public int getItemCount() {
        return memberships.size();
    }

    public void attachFragment(MembershipFragment membershipFragment) {
        this.membershipFragment = membershipFragment;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private final LinearLayout mainlayout;
        private final AppCompatTextView headingTxt;
        private final AppCompatTextView termAndtxt;
        private final AppCompatTextView priceTxt;
        private final AppCompatTextView description;
        private final AppCompatImageView image;
        private final LinearLayout joinEventLayout;
        private final AppCompatCheckBox isCheckAgrree;
        private final MaterialButton joinBtn;
        private BookingHistory bookingHistory;
        private Membership membership;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            mainlayout = itemView.findViewById(R.id.main_layout);
            image = itemView.findViewById(R.id.image_img);
            headingTxt = itemView.findViewById(R.id.membership_heading);
            priceTxt = itemView.findViewById(R.id.membership_price);
            description = itemView.findViewById(R.id.description_txt);
            joinEventLayout = itemView.findViewById(R.id.join_member_ship_layout);
            isCheckAgrree = itemView.findViewById(R.id.is_checkbox);
            joinBtn = itemView.findViewById(R.id.join_member_ship);
            termAndtxt = itemView.findViewById(R.id.txt_tems_andcon);
        }


        public void renderNavMenuName() {
            Picasso.get()
                    .load(membership.getImage())
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_foreground)
                    .into(image);
            String newheading = membership.getName().replaceAll(" ", "\n");

            headingTxt.setText(newheading);
            priceTxt.setText(NumberUtils.formatMoney(membership.getPrice()));
            description.setText(membership.getDescription());
            Utils.INSTANCE.generateClickableLink(context, termAndtxt);
        }

        public void setMemberShip(Membership membership) {
            this.membership = membership;
        }
    }
}
