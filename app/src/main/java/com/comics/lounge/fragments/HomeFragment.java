package com.comics.lounge.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.comics.lounge.ComicsLoungeApp;
import com.comics.lounge.R;
import com.comics.lounge.activity.MainActivity;
import com.comics.lounge.adapter.HomeEventListItemAdpter;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.Event;
import com.comics.lounge.modals.PopupStatus;
import com.comics.lounge.modals.PopupStatusUpdate;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.EventService;
import com.comics.lounge.webservice.manager.EventServiceManager;
import com.google.android.material.button.MaterialButton;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ServiceCallback {

    private RecyclerView rvEvent;
    private List<Event> eventList = new LinkedList<Event>();
    private EventServiceManager eventServiceManager;
    private HomeEventListItemAdpter homeEventListItemAdpter;
    private MainActivity parentActivity = null;
    private AppCompatImageView navIcon;
    private MaterialButton calenderBtn;
    private AppCompatTextView toolbarName;
    private ViewSwitcher viewSitcher;
    //private AppCompatImageView dataNotFoundImg;
    private ViewSwitcher parentSwitcher;
    private Toolbar toolbar;
    VideoView video_view;
    ImageView iv_close;
    RelativeLayout rl_video_view;
    RetroApi retroApi;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        parentSwitcher = view.findViewById(R.id.parent_switcher);
        viewSitcher = view.findViewById(R.id.view_switcher);
        toolbarName = view.findViewById(R.id.toolbar_app_name_txt);
        navIcon = view.findViewById(R.id.menuRight);
        rvEvent = view.findViewById(R.id.home_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvEvent.setLayoutManager(linearLayoutManager);
        calenderBtn = view.findViewById(R.id.calender_btn);
        //dataNotFoundImg = view.findViewById(R.id.data_not_found_icon);

        sessionManager = new SessionManager(getActivity());

        rl_video_view = view.findViewById(R.id.rl_video_view);
        video_view = view.findViewById(R.id.video_view);
        iv_close = view.findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_view.stopPlayback();
                rl_video_view.setVisibility(View.GONE);
                rvEvent.setVisibility(View.VISIBLE);
                calenderBtn.setVisibility(View.VISIBLE);
                updateStatus();
            }
        });

        getStatus();
    }

    private void getStatus() {
        retroApi = ComicsLoungeApp.getRetroApi();
        retroApi.getStatus(sessionManager.getCurrentlyLoggedUserId()).enqueue(new Callback<PopupStatus>() {
            @Override
            public void onResponse(Call<PopupStatus> call, Response<PopupStatus> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus().equals("true")){
                        rl_video_view.setVisibility(View.VISIBLE);
                        rvEvent.setVisibility(View.GONE);
                        calenderBtn.setVisibility(View.GONE);

                        video_view.setVideoURI(Uri.parse("https://www.youtube.com/embed/gw_saalfEu8?si=3tUQg08LFmWqXMik"));
                        video_view.start();
                        video_view.requestFocus();
                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                rl_video_view.setVisibility(View.GONE);
                                rvEvent.setVisibility(View.VISIBLE);
                                calenderBtn.setVisibility(View.VISIBLE);
                                updateStatus();

                            }
                        });
                    }else{
                        rl_video_view.setVisibility(View.GONE);
                        rvEvent.setVisibility(View.VISIBLE);
                        calenderBtn.setVisibility(View.VISIBLE);
                        openCalender();
                    }
                }
            }

            @Override
            public void onFailure(Call<PopupStatus> call, Throwable t) {
                rl_video_view.setVisibility(View.GONE);
                rvEvent.setVisibility(View.VISIBLE);
                calenderBtn.setVisibility(View.VISIBLE);
                openCalender();
            }
        });
    }

    private void updateStatus() {
        retroApi = ComicsLoungeApp.getRetroApi();
        retroApi.getUpdateStatus(sessionManager.getCurrentlyLoggedUserId()).enqueue(new Callback<PopupStatusUpdate>() {
            @Override
            public void onResponse(Call<PopupStatusUpdate> call, Response<PopupStatusUpdate> response) {
                openCalender();
            }

            @Override
            public void onFailure(Call<PopupStatusUpdate> call, Throwable t) {
                openCalender();
            }
        });
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



    }

    private void openCalender() {
        if (GlobalConf.checkInternetConnection(getActivity())) {
            eventServiceManager = new EventServiceManager(this, getActivity());
            eventServiceManager.prepareWebServiceJob();
            eventServiceManager.featchData();
            showingLoaderView(1, true);

            navIcon.setOnClickListener(v -> parentActivity.openDrawer());
            toolbarName.setVisibility(View.INVISIBLE);


            //dataNotFoundImg.setImageResource(R.drawable.ic_home);

            calenderBtn.setOnClickListener(v -> parentActivity.switchToCalanderFragment());
        } else {
            parentActivity.switchToNoInternetFoundActivity();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        parentActivity.callingWaletAPI();
    }

    public void showingLoaderView(int index, boolean isParent) {
        if (isParent) {
            parentSwitcher.setDisplayedChild(index);
        } else {
            viewSitcher.setDisplayedChild(index);
        }

    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }


    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(EventService.SERVICE_NAME)) {
            showingLoaderView(0, true);
            if (eventServiceManager != null && eventServiceManager.getServiceStatus() != null
                    && eventServiceManager.getServiceStatus().equals("success")) {
                eventList = eventServiceManager.getEventList();
                if (eventList.size() > 0) {
                    homeEventListItemAdpter = new HomeEventListItemAdpter(getContext(), eventList, this);
                    rvEvent.setAdapter(homeEventListItemAdpter);
                    homeEventListItemAdpter.notifyDataSetChanged();
                    showingLoaderView(0, false);
                } else {
                    showingLoaderView(1, false);
                    viewSitcher.setDisplayedChild(1);
                }
            } else {
                showingLoaderView(1, false);
                viewSitcher.setDisplayedChild(1);
            }

        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (MainActivity) context;
    }

    public void switchToEventDetailFragment(Event event) {
        parentActivity.switchToEventDetailFragment(event);
    }
}
