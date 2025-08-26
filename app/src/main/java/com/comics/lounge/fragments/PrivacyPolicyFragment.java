package com.comics.lounge.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import com.comics.lounge.R;
import com.comics.lounge.activity.MainActivity;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.utils.ToolbarUtils;

public class PrivacyPolicyFragment extends Fragment {

//    private WebViewSuite webView;
    private MainActivity parentActivity = null;
    private AppCompatImageView menuIcon;
    private LinearLayout leftIconLayout;
    private AppCompatTextView toolbarNameTxt;
    private Toolbar toolbar;
    private View llRightMenu;
    private ProgressBar progressBar;

    public static final String KEY_HIDE_LEFT_MENU = "hide_left_menu";
    private boolean isMembership;

    public static PrivacyPolicyFragment newInstant(boolean hideToolbar) {
        PrivacyPolicyFragment termsAndConditionFragment = new PrivacyPolicyFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_HIDE_LEFT_MENU, hideToolbar);
        termsAndConditionFragment.setArguments(bundle);
        return termsAndConditionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);
        if (getArguments() != null && getArguments().containsKey(KEY_HIDE_LEFT_MENU)) {
            llRightMenu.setVisibility(View.INVISIBLE);
        } else {
            llRightMenu.setVisibility(View.VISIBLE);
        }

        if (isMembership) {
            menuIcon.setVisibility(View.GONE);
        } else {
            menuIcon.setVisibility(View.VISIBLE);
        }

        ImageView ivLogo = view.findViewById(R.id.toolbar_app_logo);
        ToolbarUtils.loanAppLogo(toolbar, ivLogo);
    }

    private void findViewById(View view) {
        leftIconLayout = view.findViewById(R.id.toolbar_logo_layout);
        toolbarNameTxt = view.findViewById(R.id.toolbar_app_name_txt);
//        webView = view.findViewById(R.id.webViewSuite);
        menuIcon = view.findViewById(R.id.menuRight);
        toolbar = view.findViewById(R.id.toolbar);
        llRightMenu = view.findViewById(R.id.llRightMenu);
        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbarNameTxt.setText(getResources().getString(R.string.privacy_policy_str));

        if (parentActivity != null) menuIcon.setOnClickListener(v -> parentActivity.openDrawer());

//        webView.setCustomProgressBar(progressBar);
//        webView.startLoading(UrlCollection.PRIVACY_POLICY_URL);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) parentActivity = (MainActivity) context;
    }

    public boolean canGoBack() {
//        if (webView.goBackIfPossible()) {
//            return true;
//        }
        return false;
    }

    public void isBackPress() {
//        if (!webView.goBackIfPossible()) {
//            getFragmentManager().popBackStack();
//        }
    }

    public void isCallingFromMemberActivity(boolean isCalling) {
        isMembership = isCalling;

    }
}
