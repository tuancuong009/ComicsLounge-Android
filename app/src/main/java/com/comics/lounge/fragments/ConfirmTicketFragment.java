package com.comics.lounge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
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
import com.comics.lounge.adapter.ConfirmTicketListItemAdpter;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.ConfirmTicket;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.NetworkUtils;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.ConfirmTickertService;
import com.comics.lounge.webservice.manager.ConfirmTickertServiceManager;

public class ConfirmTicketFragment extends Fragment implements ServiceCallback {

    private RecyclerView bookingRV = null;
    private MainActivity parentActivity;
    private AppCompatImageView navIcon;
    private ConfirmTicketListItemAdpter confirmTicketListItemAdpter = null;
    private ConfirmTickertServiceManager confirmTickertServiceManager = null;
    private ViewSwitcher viewSwitcher;
    private AppCompatTextView toolbrTxtName;
    private AppCompatImageView dataNotFoundImage;
    private ViewSwitcher parentSwitcher;
    private SessionManager sessionManager;
    private Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.confirm_ticket_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);
        ImageView ivLogo = view.findViewById(R.id.toolbar_app_logo);
        ToolbarUtils.loanAppLogo(toolbar, ivLogo);
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        parentSwitcher = view.findViewById(R.id.parent_switcher);
        toolbrTxtName = view.findViewById(R.id.toolbar_app_name_txt);
        bookingRV = view.findViewById(R.id.booking_hs_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        bookingRV.setLayoutManager(linearLayoutManager);
        navIcon = view.findViewById(R.id.menuRight);
        viewSwitcher = view.findViewById(R.id.view_switcher);
        dataNotFoundImage = view.findViewById(R.id.data_not_found_icon);

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbrTxtName.setText(getText(R.string.confirmed_tickets));
        dataNotFoundImage.setImageResource(R.drawable.ic_ticket);
        navIcon.setOnClickListener(v -> parentActivity.openDrawer());
        sessionManager = new SessionManager(getActivity());

        if(NetworkUtils.getConnectivityStatus(getContext())!=0){
            confirmTickertServiceManager = new ConfirmTickertServiceManager(this, getActivity());
            fetchConfirmTickets();

            confirmTicketListItemAdpter = new ConfirmTicketListItemAdpter(getContext(), confirmTickertServiceManager.getConfirmTickets(), this);
            bookingRV.setAdapter(confirmTicketListItemAdpter);
        }else{
            Toast.makeText(getContext(),getText(R.string.internet_not_found_str),Toast.LENGTH_SHORT).show();
        }

    }

    private void switcherUI(int index, boolean isParentSwitcher) {
        if (isParentSwitcher) {
            parentSwitcher.setDisplayedChild(index);
        } else {
            viewSwitcher.setDisplayedChild(index);
        }
    }

    private void fetchConfirmTickets() {
        if (GlobalConf.checkInternetConnection(getContext())) {
            confirmTickertServiceManager.generateUrl(UrlCollection.CONFIRM_TICKET + sessionManager.getCurrentUser().getUserId());
            confirmTickertServiceManager.prepareWebServiceJob();
            confirmTickertServiceManager.featchData();
            switcherUI(1, true);
        } else {
            parentActivity.switchToNoInternetFoundActivity();
        }

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

        if (serviceName.equals(ConfirmTickertService.SERVICE_NAME)) {
            switcherUI(0, true);
            try {
                if (confirmTickertServiceManager.getServiceStatus().toLowerCase().equals("success")) {
                    if (confirmTickertServiceManager.getConfirmTickets().size() > 0) {
                        switcherUI(0, false);
                        confirmTicketListItemAdpter.notifyDataSetChanged();
                    } else {
                        switcherUI(1, false);
                    }
                } else {
                    switcherUI(1, false);
                }
            } catch (Exception e) {
                switcherUI(1, false);
            }

        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    public void switchToConfirmTicketDetailFragment(ConfirmTicket confirmTicket) {
        if (GlobalConf.checkInternetConnection(getActivity())) {
            parentActivity.switchToConfirmTicketDetailFragment(confirmTicket);
        } else {
            parentActivity.switchToNoInternetFoundActivity();
        }

    }
}
