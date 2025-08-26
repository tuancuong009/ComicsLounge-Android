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
import com.comics.lounge.modals.NavMenuItem;

import java.util.List;

public class NavMenuListItemAdpter extends RecyclerView.Adapter<NavMenuListItemAdpter.ItemHolder> {
    private final View.OnClickListener itemListClick;
    private List<NavMenuItem> navMenuItemList = null;
    private Context context;
    private LayoutInflater layoutInflater;

    public NavMenuListItemAdpter(Context applicationContext, List<NavMenuItem> navMenuItemList, View.OnClickListener itemListClick) {
        this.context = applicationContext;
        this.navMenuItemList = navMenuItemList;
        this.itemListClick = itemListClick;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.nav_menu_item_cell_layout, parent, false);
        return new ItemHolder(view, itemListClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.setNavData(navMenuItemList.get(position));
        holder.renderNavMenuName();
    }

    @Override
    public int getItemCount() {
        return navMenuItemList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView nameMenu;
        private final View.OnClickListener itemListClick;
        private final AppCompatImageView leftMenuIcon;
        private LinearLayout linerMainLayout;
        private NavMenuItem navMenuItem;

        public ItemHolder(@NonNull View itemView, View.OnClickListener itemListClick) {
            super(itemView);
            this.itemListClick = itemListClick;
            nameMenu = itemView.findViewById(R.id.nav_menu_name_txt);
            linerMainLayout = itemView.findViewById(R.id.liner_main_layout);
            leftMenuIcon = itemView.findViewById(R.id.left_menu_icon);
        }

        public void setNavData(NavMenuItem navMenuItem) {
            this.navMenuItem = navMenuItem;
        }

        public void renderNavMenuName() {
            nameMenu.setText(navMenuItem.getNavMenuName().trim());
            linerMainLayout.setTag(navMenuItem);
            linerMainLayout.setOnClickListener(itemListClick);
            leftMenuIcon.setImageResource(navMenuItem.getNavMenuImg());
        }
    }
}
