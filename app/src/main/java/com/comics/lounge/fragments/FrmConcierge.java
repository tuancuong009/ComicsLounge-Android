package com.comics.lounge.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.comics.lounge.R;
import com.comics.lounge.activity.NewMain;
import com.comics.lounge.databinding.FrmConciergeBinding;
import com.comics.lounge.databinding.PopupMemberVideoBinding;
import com.comics.lounge.utils.AppUtil;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomSpace;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.intercom.android.sdk.UnreadConversationCountListener;
import io.intercom.android.sdk.identity.Registration;


public class FrmConcierge extends Fragment {
    FrmConciergeBinding binding;
    NewMain activity;
    public FrmConcierge() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (NewMain) getActivity();
        binding = FrmConciergeBinding.inflate(getLayoutInflater());
        init();

        binding.rlQa.setOnClickListener(v -> activity.addFrmDetail(new FrmQA()));
        binding.rlMbVideo.setOnClickListener(v -> activity.popupVideo());
        binding.rlHelpUsing.setOnClickListener(v -> activity.addFrmDetail(new FrmHelpUsingMb()));
        binding.rlMbNotification.setOnClickListener(v -> {
            Registration registration = Registration.create().withUserId(activity.sessionManager.getCurrentlyLoggedUserId());
            Intercom.client().loginIdentifiedUser(registration, new IntercomStatusCallback() {
                @Override
                public void onSuccess() {
                    // Handle success
                }

                @Override
                public void onFailure(@NonNull IntercomError intercomError) {
                    // Handle failure
                }
            });
            Intercom.client().present(IntercomSpace.Home);
        });

        return binding.getRoot();
    }
    
    // init UI
    private void init(){
        if (Intercom.client().getUnreadConversationCount() > 0){
            binding.cvUnread.setVisibility(View.VISIBLE);
            binding.tvUnread.setText(String.valueOf(Intercom.client().getUnreadConversationCount()));
        }else {
            binding.cvUnread.setVisibility(View.GONE);
        }
        Intercom.client().addUnreadConversationCountListener(i -> {
            if (i > 0){
                binding.cvUnread.setVisibility(View.VISIBLE);
                binding.tvUnread.setText(String.valueOf(Intercom.client().getUnreadConversationCount()));
            }else {
                binding.cvUnread.setVisibility(View.GONE);
            }
        });
//        if (Boolean.TRUE.equals(activity.sessionManager.getCurrentUser().getMembership())){
//            binding.rlMbNotification.setVisibility(View.VISIBLE);
//        }else {
//            binding.rlMbNotification.setVisibility(View.GONE);
//        }
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