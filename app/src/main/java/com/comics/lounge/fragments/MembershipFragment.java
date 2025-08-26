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
import com.comics.lounge.activity.MembershipActivity;
import com.comics.lounge.activity.PromoCodeActivity;
import com.comics.lounge.activity.WebViewActivity;
import com.comics.lounge.adapter.MembershipItemCellAdpter;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.Membership;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.MembershipService;
import com.comics.lounge.webservice.manager.MembershipServiceManager;

import static com.comics.lounge.activity.PromoCodeActivity.EXTRA_MEMBERSHIP;

public class MembershipFragment extends Fragment implements ServiceCallback {

    private RecyclerView memberShipRV;
    private MembershipServiceManager membershipServiceManager = null;
    private MembershipItemCellAdpter membershipItemCellAdpter = null;
    private MainActivity parentActivity;
    private AppCompatImageView menuRight;
    private AppCompatTextView toolbarName;
    private LinearLayout toolbarLogoLayout;
    private ViewSwitcher viewSwitcher;
    private MembershipActivity parentMembershipActivity;
    private AppCompatImageView dataNotFoundImg;
    private SessionManager sessionManager;
    private ViewSwitcher parentViewSwitcher = null;
    private AppCompatTextView toolbarskiptxt = null;
    private Toolbar toolbar;
    private ImageView ivLogo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.membership_list_fragment, container, false);
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        toolbarLogoLayout = view.findViewById(R.id.toolbar_logo_layout);
        toolbarName = view.findViewById(R.id.toolbar_app_name_txt);
        parentViewSwitcher = view.findViewById(R.id.parnet_switcher_layout);
        viewSwitcher = view.findViewById(R.id.view_switcher);
        memberShipRV = view.findViewById(R.id.member_ship_rv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        memberShipRV.setLayoutManager(linearLayoutManager);
        menuRight = view.findViewById(R.id.menuRight);
        toolbarskiptxt = (AppCompatTextView) view.findViewById(R.id.toolbar_skip_name_txt);
        dataNotFoundImg = view.findViewById(R.id.data_not_found_icon);
        ivLogo = view.findViewById(R.id.toolbar_app_logo);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        if (parentActivity != null) {
            menuRight.setVisibility(View.VISIBLE);
            toolbarskiptxt.setVisibility(View.GONE);
            ToolbarUtils.loanAppLogo(toolbar, ivLogo);
        } else {
            menuRight.setVisibility(View.GONE);
            toolbarskiptxt.setVisibility(View.VISIBLE);
        }
        displayViewSwitcher(parentViewSwitcher, 1);

        toolbarName.setText(getResources().getString(R.string.member_ship_str));

        ImageView ivLogo = toolbarLogoLayout.findViewById(R.id.toolbar_app_logo);
        ToolbarUtils.loanAppLogo(toolbar, ivLogo);

        membershipServiceManager = new MembershipServiceManager(this, getActivity());
        membershipServiceManager.prepareWebServiceJob();
        membershipServiceManager.featchData();
        membershipItemCellAdpter = new MembershipItemCellAdpter(getContext(),
                membershipServiceManager.getMemberships(), sessionManager.getCurrentUser().getMemershipId());
        membershipItemCellAdpter.attachFragment(this);
        memberShipRV.setAdapter(membershipItemCellAdpter);

        dataNotFoundImg.setImageResource(R.drawable.ic_membership);

        // callingUpdateSession();
        toolbarskiptxt.setOnClickListener(v -> {
            switchtoDashboardFragment();
        });


        menuRight.setOnClickListener(v -> {
            if (parentActivity != null) {
                parentActivity.openDrawer();
            }
        });
    }

    public void switchToTemsandCondiFrafment() {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.terms_condition_str));
        intent.putExtra(WebViewActivity.EXTRA_URL, UrlCollection.TEMS_CON_URL);
        startActivity(intent);

        /*if (parentMembershipActivity != null) {
            menuRight.setVisibility(View.GONE);
            toolbarskiptxt.setVisibility(View.GONE);
            parentMembershipActivity.switchTotemsAndConditionFragment();
        } else {
            menuRight.setVisibility(View.GONE);
            toolbarskiptxt.setVisibility(View.GONE);
            parentActivity.switchTotemsAndConditionFragment();
        }*/
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
        if (context instanceof MainActivity) {
            parentActivity = (MainActivity) context;
        } else if (context instanceof MembershipActivity) {
            parentMembershipActivity = (MembershipActivity) context;
        }
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(MembershipService.SERVICE_NAME)) {
            displayViewSwitcher(parentViewSwitcher, 0);
            if (membershipServiceManager.getServiceStatus().equals("success")) {
                if (membershipServiceManager.getMemberships().size() > 0) {
                    displayViewSwitcher(viewSwitcher, 0);
                    membershipItemCellAdpter.notifyDataSetChanged();
                } else {
                    displayViewSwitcher(viewSwitcher, 1);
                }
            } else {
                displayViewSwitcher(viewSwitcher, 1);
            }
        }
    }

    public void displaySnackbarMessage(String snackbarMessage) {
        if (parentActivity != null) {
            parentActivity.displaySnackbarMsg(snackbarMessage);
        } else {
            parentMembershipActivity.displaySnackbarMsg(snackbarMessage);
        }
    }

    public void switchToPaymentScreen(Membership membership) {
        Intent intent = new Intent(getContext(), PromoCodeActivity.class);
        intent.putExtra(EXTRA_MEMBERSHIP, membership);
        startActivity(intent);
    }

    public void switchtoDashboardFragment() {
        if (parentMembershipActivity != null) {
            parentMembershipActivity.switchToDashboardFragment();
        }
    }
}
