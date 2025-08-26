package com.comics.lounge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.comics.lounge.R;
import com.comics.lounge.customeview.TouchImageView;
import com.comics.lounge.utils.ToolbarUtils;
import com.squareup.picasso.Picasso;

public class FullScreenImageViewActivity extends AbstractBaseActivity {
    private TouchImageView touchImageView;
    private String userId;
    private String imageUrl;
    private AppCompatTextView toolbarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_view);

        toolbarName = findViewById(R.id.toolbar_app_name_txt);
        touchImageView = findViewById(R.id.touch_image_view);

        Intent intent = getIntent();
        if (intent != null) {
            imageUrl = intent.getStringExtra("imageURL");
        }

        toolbarName.setText(getResources().getString(R.string.zoom_image_view));
        Toolbar toolbar = findViewById(R.id.toolbar);
        ToolbarUtils.showBackArrow(toolbar, this);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher_foreground)
                .error(R.mipmap.ic_launcher_foreground)
                .into(touchImageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
