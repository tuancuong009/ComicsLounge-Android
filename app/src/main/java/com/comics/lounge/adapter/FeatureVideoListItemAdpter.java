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
import com.comics.lounge.fragments.FeaturedVideoFragment;
import com.comics.lounge.modals.FeatureVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeatureVideoListItemAdpter extends RecyclerView.Adapter<FeatureVideoListItemAdpter.ItemHolder> {
    private FeaturedVideoFragment featuredVideoFragment;
    private List<FeatureVideo> featureVideoList = null;
    private Context context;
    private LayoutInflater layoutInflater;


    public FeatureVideoListItemAdpter(Context context, List<FeatureVideo> itemList, FeaturedVideoFragment featuredVideoFragment) {
        this.context = context;
        this.featureVideoList = itemList;
        this.featuredVideoFragment = featuredVideoFragment;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.video_item_cell, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
        holder.setVideoObj(featureVideoList.get(position));
        holder.renderEventName();

        holder.cardView.setOnClickListener(v -> featuredVideoFragment.switchToYoutubeActivity(featureVideoList.get(position)));
    }

    @Override
    public int getItemCount() {
        return featureVideoList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView videImage;
        private final AppCompatTextView videTitle;
        private final LinearLayout cardView;
        private FeatureVideo featureVideo;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.video_card_obj);
            videImage = itemView.findViewById(R.id.video_image);
            videTitle = itemView.findViewById(R.id.video_title);
            videImage.setClipToOutline(true);
        }


        public void renderEventName() {
            videTitle.setText(featureVideo.getName());
            Picasso.get().load(featureVideo.getImage())
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_foreground)
                    .into(videImage);

        }

        public void setVideoObj(FeatureVideo featureVideo) {
            this.featureVideo = featureVideo;
        }
    }
}
