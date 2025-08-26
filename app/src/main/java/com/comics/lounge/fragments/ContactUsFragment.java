package com.comics.lounge.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.comics.lounge.R;
import com.comics.lounge.activity.MainActivity;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.ContactUsService;
import com.comics.lounge.webservice.manager.ContactUsServiceManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;

public class ContactUsFragment extends Fragment implements ServiceCallback {

    private AppCompatImageView navIcon;
    private MainActivity parentActivity;
    private MaterialButton submitBtn;
    private AppCompatEditText messageEdt;
    private ContactUsServiceManager contactUsServiceManager = null;
    private AppCompatTextView toolbarName;
    private AppCompatImageView googleMapMarker;
    private AppCompatTextView phoneNOTxt;
    private AppCompatTextView emailAddressTxt;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private ViewSwitcher parentSwitche;
    private SessionManager sessionManager;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_us_fragment, container, false);
        findViewById(view);
        return view;
    }

    private void findViewById(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        parentSwitche = view.findViewById(R.id.parent_switcher);
        toolbarName = view.findViewById(R.id.toolbar_app_name_txt);
        navIcon = view.findViewById(R.id.menuRight);
        submitBtn = view.findViewById(R.id.submit_msg);
        messageEdt = view.findViewById(R.id.message_edt);
        googleMapMarker = view.findViewById(R.id.google_map_marker);
        phoneNOTxt = view.findViewById(R.id.phone_no_txt);
        emailAddressTxt = view.findViewById(R.id.email_address_txt);

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
        sessionManager = new SessionManager(getActivity());
        toolbarName.setText(getResources().getString(R.string.contact_us_heading));
        contactUsServiceManager = new ContactUsServiceManager(this, getActivity());
        contactUsServiceManager.generateUrl(UrlCollection.CONTACT_US + "");

        emailAddressTxt.setOnClickListener(v -> {
            try {
                if (GlobalConf.checkInternetConnection(getActivity())) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + getResources().getString(R.string.app_email_str)));
                    startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                } else {
                    parentActivity.switchToNoInternetFoundActivity();
                }

            } catch (Exception e) {
                parentActivity.displaySnackbarMsg(getResources().getString(R.string.action_not_found_str));
            }

        });

        googleMapMarker.setOnClickListener(v -> {
            if (GlobalConf.checkInternetConnection(getContext())) {
                parentActivity.switchToGoogleMapactivity();
               /* try {
                    Uri gmmIntentUri = Uri.parse("geo:-37.8045557,144.9476425?q=" + getResources().getString(R.string.contact_us_add_str));
                    //String url = "https://www.google.com/maps/search/?api=1&query=" + getResources().getString(R.string.contact_us_add_str);
                    Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } catch (Exception e) {
                    parentActivity.displaySnackbarMsg(getResources().getString(R.string.action_not_found_str));
                }*/
            } else {
                parentActivity.switchToNoInternetFoundActivity();
            }


        });

        phoneNOTxt.setOnClickListener(v -> {
            if (GlobalConf.checkInternetConnection(getContext())) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + getResources().getString(R.string.app_phone_str)));
                    startActivity(intent);
                } catch (Exception e) {
                    parentActivity.displaySnackbarMsg(getResources().getString(R.string.action_not_found_str));
                }
            } else {
                parentActivity.switchToNoInternetFoundActivity();
            }


        });

        submitBtn.setOnClickListener(v -> {
            if (AppUtil.isBlank(messageEdt, "Message cannot be blank")) {

            } else {
                if (GlobalConf.checkInternetConnection(getContext())) {
                    parentActivity.hideKeyboardFrom();
                    contactUsServiceManager.generateUrl(UrlCollection.CONTACT_US + "user_id=" + sessionManager.getCurrentlyLoggedUserId() + "&message=" + messageEdt.getText().toString().trim());
                    contactUsServiceManager.prepareWebServiceJob();
                    contactUsServiceManager.featchData();
                    displayViewSwitcher(parentSwitche, 1);
                } else {
                    parentActivity.switchToNoInternetFoundActivity();
                }
            }
        });

        navIcon.setOnClickListener(v -> parentActivity.openDrawer());
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
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(ContactUsService.SERVICE_NAME)) {
            displayViewSwitcher(parentSwitche, 0);
            if (contactUsServiceManager.getServiceStatus().equals("Success")) {
                messageEdt.setText("");
                parentActivity.displaySnackbarMsg(contactUsServiceManager.getResponseMsg());
            }
        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }


}
