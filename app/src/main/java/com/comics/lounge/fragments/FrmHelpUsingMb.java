package com.comics.lounge.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import android.text.Annotation;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmHelpUsingMbBinding;
import com.comics.lounge.databinding.PopupMemberVideoBinding;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.CustomClickSpan;


public class FrmHelpUsingMb extends Fragment {
    FrmHelpUsingMbBinding binding;
    NewMain activity;
    public FrmHelpUsingMb() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmHelpUsingMbBinding.inflate(getLayoutInflater());
        init();

        return binding.getRoot();
    }

    // init UI
    private void init(){
        SpannedString termsText = (SpannedString) getText(R.string.help_by_video_qa);
        Annotation[] annotations = termsText.getSpans(0, termsText.length(), Annotation.class);
        SpannableString termsCopy = new SpannableString(termsText);
        for (Annotation annotation : annotations) {
            if (annotation.getKey().equals("action")) {
                termsCopy.setSpan(
                        createClickSpan(annotation.getValue()),
                        termsText.getSpanStart(annotation),
                        termsText.getSpanEnd(annotation),
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        binding.tvInstruct.setText(termsCopy);
        binding.tvInstruct.setMovementMethod(LinkMovementMethod.getInstance());
        binding.ivEvent.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(activity).widthPixels / 3.638)));
        binding.ivPf.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (AppUtil.getScreenSize(activity).widthPixels / 2.207)));
    }

    // create link span
    private CustomClickSpan createClickSpan(String action) {
        switch (action.toLowerCase()) {
            case "video":
                return new CustomClickSpan(() -> activity.popupVideo(), "#EC027D");
            case "qa":
                return new CustomClickSpan(() -> activity.addFrmDetail(new FrmQA()), "#EC027D");
            default:
                throw new UnsupportedOperationException("action " + action + " not implemented");
        }
    }

//    // popup membership video
//    private void popupVideo(){
//        Dialog dialog = new Dialog(activity);
//        PopupMemberVideoBinding videoBinding = PopupMemberVideoBinding.inflate(getLayoutInflater());
//        dialog.setContentView(videoBinding.getRoot());
//        AppUtil.setupDialog(dialog, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        String path = "https://www.youtube.com/embed/gw_saalfEu8?si=3tUQg08LFmWqXMik";
//        ExoPlayer player = new ExoPlayer.Builder(activity).build();
//        videoBinding.videoView.setPlayer(player);
//        MediaItem mediaItem = MediaItem.fromUri(path);
//        player.setMediaItem(mediaItem);
//        player.prepare();
//        player.play();
//        videoBinding.btClose.setOnClickListener(v -> {
//            player.stop();
//            player.release();
//            dialog.dismiss();
//        });
//        dialog.setOnDismissListener(dialog1 -> {
//            player.stop();
//            player.release();
//        });
//
//        dialog.show();
//    }
}