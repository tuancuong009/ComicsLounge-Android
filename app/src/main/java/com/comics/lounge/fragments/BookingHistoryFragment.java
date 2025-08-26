package com.comics.lounge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.comics.lounge.adapter.BookingListItemAdpter;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.BookingHistoryService;
import com.comics.lounge.webservice.manager.BookigHistoryServiceManager;

import java.text.SimpleDateFormat;

public class BookingHistoryFragment extends Fragment implements ServiceCallback {
    private RecyclerView bookingRV = null;
    private BookingListItemAdpter bookingListItemAdpter = null;
    private MainActivity parentActivity;
    private AppCompatImageView navIcon;
    private BookigHistoryServiceManager bookigHistoryServiceManager = null;
    private String userId;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;
    private AppCompatTextView toolbarNameTxt;
    private ViewSwitcher viewSwitcher;
    private AppCompatImageView bookingNotFoundIcon;
    private ViewSwitcher parentViewSwitcher;
    private SessionManager sessionManager;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booking_history_fragment, container, false);
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        parentViewSwitcher = view.findViewById(R.id.parent_view_switcher);
        viewSwitcher = view.findViewById(R.id.view_switcher);
        toolbarNameTxt = view.findViewById(R.id.toolbar_app_name_txt);
        bookingRV = view.findViewById(R.id.booking_hs_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        bookingRV.setLayoutManager(linearLayoutManager);
        navIcon = view.findViewById(R.id.menuRight);

        bookingNotFoundIcon = view.findViewById(R.id.data_not_found_icon);
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

        toolbarNameTxt.setText(getResources().getString(R.string.booking_history));

        sessionManager = new SessionManager(getActivity());

        navIcon.setOnClickListener(v -> parentActivity.openDrawer());

        inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        outputFormat = new SimpleDateFormat("dd MMMM yyyy");

        bookigHistoryServiceManager = new BookigHistoryServiceManager(this, getActivity());
        bookigHistoryServiceManager.generateUrl(UrlCollection.BOOKING_HISTORY + sessionManager.getCurrentUser().getUserId());
        bookigHistoryServiceManager.prepareWebServiceJob();
        bookigHistoryServiceManager.featchData();
        displayViewSwitcher(parentViewSwitcher, 1);


        bookingListItemAdpter = new BookingListItemAdpter(getContext(), bookigHistoryServiceManager.getBookingHistoryList(), this);
        bookingListItemAdpter.attachFormate(inputFormat, outputFormat);
        bookingRV.setAdapter(bookingListItemAdpter);

        bookingNotFoundIcon.setImageResource(R.drawable.ic_history);
    }

    /**
     * @param viewSwitcher obj
     * @param displayIndex index
     */
    public void displayViewSwitcher(ViewSwitcher viewSwitcher, int displayIndex) {
        viewSwitcher.setDisplayedChild(displayIndex);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (MainActivity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {

        if (serviceName.equals(BookingHistoryService.SERVICE_NAME)) {
            displayViewSwitcher(parentViewSwitcher, 0);
            if (bookigHistoryServiceManager.getServiceStatus().toLowerCase().equals("success")) {
                if (bookigHistoryServiceManager.getBookingHistoryList().size() > 0) {
                    displayViewSwitcher(viewSwitcher, 0);
                    bookingListItemAdpter.notifyDataSetChanged();
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
}
