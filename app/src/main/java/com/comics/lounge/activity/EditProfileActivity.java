package com.comics.lounge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.comics.lounge.R;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.user.User;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.ToolbarUtils;
import com.comics.lounge.webservice.EditProfileService;
import com.comics.lounge.webservice.manager.EditProfileServiceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AbstractBaseActivity implements ServiceCallback {

    private Toolbar toolbar;
    private AppCompatTextView toolbarName;
    private TextInputEditText firstNameEdt, phoneNoEdt, countryCodeEdt;
    private MaterialButton updateBtn;
    private EditProfileServiceManager editProfileServiceManager;
    private ViewSwitcher viewSwitcher;
    private LinearLayout mainLayout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mainLayout = findViewById(R.id.maian_layout);
        sessionManager = new SessionManager(this);
        //InternetAvailabilityChecker.init(this);
        viewSwitcher = findViewById(R.id.view_switcher);
        toolbar = findViewById(R.id.toolbar);
        toolbarName = toolbar.findViewById(R.id.toolbar_app_name_txt);

        toolbarName.setText(getResources().getString(R.string.edit_profile));
        firstNameEdt = findViewById(R.id.first_name_edt);
        phoneNoEdt = findViewById(R.id.phone_number_edt);
        countryCodeEdt = findViewById(R.id.country_code_edt);
        updateBtn = findViewById(R.id.update_btn);

        ToolbarUtils.showBackArrow(toolbar, this);

        editProfileServiceManager = new EditProfileServiceManager(this, this);

        User currentUser = sessionManager.getCurrentUser();
        firstNameEdt.setText(currentUser.getName());
        phoneNoEdt.setText(currentUser.getMobile());
        countryCodeEdt.setText(currentUser.getCountryCode());

        updateBtn.setOnClickListener(v -> {
            if (AppUtil.isBlank(firstNameEdt, "Firstname cannot be blank.")) {

            } else if (AppUtil.isBlank(phoneNoEdt, "Phone number cannot be blank.")) {

            } else if (AppUtil.isBlank(countryCodeEdt, "Country code cannot be blank.")) {

            } else {
                if (GlobalConf.checkInternetConnection(getApplicationContext())) {
                    JSONObject editProfile = new JSONObject();
                    try {
                        editProfile.put("customer_id", sessionManager.getCurrentUser().getUserId());
                        editProfile.put("name", firstNameEdt.getText().toString().trim());
                        editProfile.put("phone_number", phoneNoEdt.getText().toString().trim());
                        editProfile.put("country_phone_code", countryCodeEdt.getText().toString().trim());
                        editProfileServiceManager.prepareWebServiceJob();
                        editProfileServiceManager.feedParamsWithoutKey(editProfile.toString());
                        editProfileServiceManager.featchData();
                        viewSwitcher.showNext();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    switchToNoInternetFoundActivity();
                }

            }
        });
    }

    private void switchToNoInternetFoundActivity() {
        Intent intent = new Intent(EditProfileActivity.this, NoInternetActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        if (serviceName.equals(EditProfileService.SERVICE_NAME)) {
            viewSwitcher.showPrevious();
            if (editProfileServiceManager.getServiceStatus() != null) {
                if (editProfileServiceManager.getServiceStatus().equals("success")) {

                    User currentUser = sessionManager.getCurrentUser();
                    currentUser.setName(firstNameEdt.getText().toString().trim());
                    currentUser.setMobile(phoneNoEdt.getText().toString().trim());
                    currentUser.setCountryCode(countryCodeEdt.getText().toString().trim());
                    sessionManager.createOrUpdateLogin(currentUser);

                    Snackbar.make(mainLayout, editProfileServiceManager.getResponseMsg(), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
