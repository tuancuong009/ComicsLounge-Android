package com.comics.lounge.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.comics.lounge.R;
import com.comics.lounge.databinding.ActivityVideoPlayerBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

public class VideoPlayer extends AppCompatActivity {
    ActivityVideoPlayerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.btClose.setOnClickListener(v -> finish());
    }

    // init UI
    private void init(){
        String url = getIntent().getStringExtra("url");
        if (url != null){
            String id = url.substring(url.indexOf("watch?v=")).replace("watch?v=", "");
            getLifecycle().addObserver(binding.ytPlayer);
            YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    DefaultPlayerUiController controller = new DefaultPlayerUiController(binding.ytPlayer, youTubePlayer);
                    binding.ytPlayer.setCustomPlayerUi(controller.getRootView());
                    youTubePlayer.loadVideo(id, 0f);
                }
            };
            IFramePlayerOptions options = new IFramePlayerOptions.Builder()
                    .controls(0)
                    .rel(0)
                    .ivLoadPolicy(1)
                    .ccLoadPolicy(1)
                    .build();
            binding.ytPlayer.initialize(listener, options);
            binding.ytPlayer.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                @Override
                public void onYouTubePlayerEnterFullScreen() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                @Override
                public void onYouTubePlayerExitFullScreen() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.ytPlayer.release();
    }
}