package com.comics.lounge.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.R;
import com.comics.lounge.activity.MainActivity;
import com.comics.lounge.activity.NoInternetActivity;
import com.comics.lounge.activity.WebViewActivity;
import com.comics.lounge.adapter.FeatureVideoListItemAdpter;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.FeatureVideo;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.FeatureVideoService;
import com.comics.lounge.webservice.manager.FeaturedVideosServiceManager;

public class FeaturedVideoFragment extends Fragment implements ServiceCallback {

    private AppCompatImageView menuRight;
    private MainActivity parentActivity = null;
    private FeaturedVideosServiceManager featuredVideosServiceManager = null;
    private FeatureVideoListItemAdpter featureVideoListItemAdpter = null;
    private RecyclerView fetVideoRV;
    private AppCompatTextView toolbarName;
    private LinearLayout toolbarLogoLayout;
    private ViewSwitcher viewSwitcher;
    private AppCompatImageView dataNotFoundImg;
    private ViewSwitcher parentSwitcher;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feature_video_list_fragment, container, false);
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        parentSwitcher = view.findViewById(R.id.parent_switcher);
        viewSwitcher = view.findViewById(R.id.view_switcher);
        toolbarLogoLayout = view.findViewById(R.id.toolbar_logo_layout);
        toolbarName = view.findViewById(R.id.toolbar_app_name_txt);
        menuRight = view.findViewById(R.id.menuRight);
        fetVideoRV = view.findViewById(R.id.feature_video_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        fetVideoRV.setLayoutManager(linearLayoutManager);
        dataNotFoundImg = view.findViewById(R.id.data_not_found_icon);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView ivLogo = view.findViewById(R.id.toolbar_app_logo);
        ToolbarUtils.loanAppLogo(toolbar, ivLogo);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbarLogoLayout.setVisibility(View.VISIBLE);
        toolbarName.setText(getResources().getString(R.string.feat_video_str));
        menuRight.setOnClickListener(v -> parentActivity.openDrawer());


        featuredVideosServiceManager = new FeaturedVideosServiceManager(this, getActivity());
        featuredVideosServiceManager.prepareWebServiceJob();
        featuredVideosServiceManager.featchData();

        displayViewSwitcher(parentSwitcher, 1);

        featureVideoListItemAdpter = new FeatureVideoListItemAdpter(getContext(), featuredVideosServiceManager.getVideoList(), this);
        fetVideoRV.setAdapter(featureVideoListItemAdpter);
        dataNotFoundImg.setImageResource(R.drawable.ic_play_button);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (MainActivity) context;
    }

    /**
     * @param viewSwitcher obj
     * @param displayIndex index
     */
    public void displayViewSwitcher(ViewSwitcher viewSwitcher, int displayIndex) {
        viewSwitcher.setDisplayedChild(displayIndex);
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(FeatureVideoService.SERVICE_NAME)) {
            displayViewSwitcher(parentSwitcher, 0);
            if (featuredVideosServiceManager.getServiceStatus().equals("success")) {
                if (featuredVideosServiceManager.getVideoList().size() > 0) {
                    displayViewSwitcher(viewSwitcher, 0);
                    featureVideoListItemAdpter.notifyDataSetChanged();
                } else {
                    displayViewSwitcher(viewSwitcher, 1);
                }
            } else {
                displayViewSwitcher(viewSwitcher, 1);
            }
        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    public void switchToYoutubeActivity(FeatureVideo featureVideo) {
        if (GlobalConf.checkInternetConnection(getContext())) {
            Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_TITLE, featureVideo.getName());
            intent.putExtra(WebViewActivity.EXTRA_URL, featureVideo.getLink());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), NoInternetActivity.class);
            startActivity(intent);
        }
    }
}
