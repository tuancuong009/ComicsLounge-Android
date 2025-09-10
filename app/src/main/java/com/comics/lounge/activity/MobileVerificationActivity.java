package com.comics.lounge.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.comics.lounge.R;
import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.user.User;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.sessionmanager.SessionManager;
import com.comics.lounge.utils.AppUtil;
import com.comics.lounge.utils.DialogUtils;
import com.comics.lounge.utils.NumberUtils;
import com.comics.lounge.webservice.EditProfileService;
import com.comics.lounge.webservice.OtpSendService;
import com.comics.lounge.webservice.OtpVerifyService;
import com.comics.lounge.webservice.RegisterService;
import com.comics.lounge.webservice.manager.EditProfileServiceManager;
import com.comics.lounge.webservice.manager.OTPSendServiceManager;
import com.comics.lounge.webservice.manager.OTPVerifyServiceManager;
import com.comics.lounge.webservice.manager.RegisterServiceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.ozcanalasalvar.otp_view.view.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.comics.lounge.activity.EmailOTPVerifyActivity.EXTRA_USER_EMAIL;
import static com.comics.lounge.activity.MobileVerificationActivity.ScreeFlow.EDIT_PROFILE;
import static com.comics.lounge.activity.MobileVerificationActivity.ScreeFlow.FROM_HOME;
import static com.comics.lounge.activity.MobileVerificationActivity.ScreeFlow.FROM_REGISTER;
import static com.comics.lounge.activity.MobileVerificationActivity.ScreeFlow.OTP_VIEW;
import static com.comics.lounge.conf.Constant.OTP_TYPE_PHONE;
import static com.comics.lounge.conf.Constant.SUCCESS;
import static com.comics.lounge.conf.Constant.VERIFIED;

public class MobileVerificationActivity extends AbstractBaseActivity implements ServiceCallback {

    String userId = "";
    //String countryCode = "+61";

    public static String EXTRA_SCREEN_FLOW = "screen_flow";
    public static String EXTRA_USER_ID = "user_id";

    private ViewSwitcher viewSwitcher;
    private AppCompatButton saveOtpBtn;
    private AppCompatEditText mobileNoEdt;
    private OtpView verifiCOdeEdt;
    private AppCompatButton doneBtn;
    private OTPSendServiceManager otpSendServiceManager = null;
    private OTPVerifyServiceManager otpVerifyServiceManager = null;
    private AppCompatEditText countryCodeTxt;
    private Dialog dialog;
    private SessionManager sessionManager;
    private EditProfileServiceManager editProfileServiceManager;
    private RegisterServiceManager registerServiceManager;
    private String regJson = null;
    private ViewSwitcher loaderSwitcher;
    private ScreeFlow screenFlow;
    private ImageView imgHome,imgHome1 ;
    String otpCode = "";
    AppCompatTextView tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        loaderSwitcher = findViewById(R.id.loaderSwitcher);
        viewSwitcher = findViewById(R.id.view_switcher);
        saveOtpBtn = findViewById(R.id.save_otp_btn);
        mobileNoEdt = findViewById(R.id.mobile_no_edt);
        countryCodeTxt = findViewById(R.id.country_code_str);
        tvPhone = findViewById(R.id.tv_phone);

        verifiCOdeEdt = findViewById(R.id.enter_code_edi);
        doneBtn = findViewById(R.id.done_otp_btn);
        imgHome = findViewById(R.id.imgHome);
        imgHome1 = findViewById(R.id.imgHome1);


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.loder_layout);
        dialog.setCancelable(false);


      //  countryCodeTxt.setText(countryCode);
        sessionManager = new SessionManager(this);
        otpSendServiceManager = new OTPSendServiceManager(this, this);
        registerServiceManager = new RegisterServiceManager(this, this);
        otpVerifyServiceManager = new OTPVerifyServiceManager(this, this);
        editProfileServiceManager = new EditProfileServiceManager(this, this);

        Intent intent = getIntent();
        if (intent != null) {

            screenFlow = (ScreeFlow) intent.getSerializableExtra(EXTRA_SCREEN_FLOW);
            if (intent.hasExtra(EXTRA_USER_ID)) {
                this.userId = intent.getStringExtra(EXTRA_USER_ID);
            }

            if (screenFlow == FROM_REGISTER) {
                regJson = intent.getStringExtra("regJson");
                AppUtil.focusKeyboard(mobileNoEdt, this);
            } else if (screenFlow == OTP_VIEW) {
                viewSwitcher.showNext();    //show verify otp screen view
            } else if (screenFlow == EDIT_PROFILE || screenFlow == FROM_HOME) {
                mobileNoEdt.setText(sessionManager.getCurrentUser().getMobile());
                this.userId = sessionManager.getCurrentUser().getUserId();
            }
        }
        verifiCOdeEdt.setTextChangeListener((s, b) -> {
            if (b){
                otpCode = s;
            }else {
                otpCode = "";
            }
        });
        saveOtpBtn.setOnClickListener(v -> {
            if (validate()) return;
            String number = NumberUtils.removeZeroIf(mobileNoEdt);
            if (number.length() <= 8) {
                mobileNoEdt.setError(getString(R.string.enter_8_above_num));
                return;
            }

            if (screenFlow == FROM_REGISTER) {

                DialogUtils.showInfoAlert(
                        MobileVerificationActivity.this,
                        getString(R.string.app_name),
                        getString(R.string.before_register),
                        "Ok",
                        "Edit",
                        true,
                        positiveButtonClicked -> {
                            if (positiveButtonClicked) {
                                try {
                                    JSONObject stringCOnObj = new JSONObject(regJson);
                                    stringCOnObj.put("phone_number", number);
                                    stringCOnObj.put("country_phone_code", "61");
                                    registerServiceManager.prepareWebServiceJob();
                                    registerServiceManager.feedParamsWithoutKey(stringCOnObj.toString());
                                    registerServiceManager.featchData();
                                    showSwitcher(loaderSwitcher, 1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //Edit register details
                                Intent intentt= new Intent(MobileVerificationActivity.this,
                                        RegistrationActivity.class);
                                intentt.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intentt);
                            }
                            return null;
                        }
                );

            } else if (screenFlow == FROM_HOME) {
                otpSendServiceManager.prepareWebServiceJob();
                JSONObject otpObj = new JSONObject();
                try {
                    otpObj.put("user_id", userId);
                    otpObj.put("country_phone_code", "61");
                    otpObj.put("phone_number", number);
                    otpObj.put("otp_type", OTP_TYPE_PHONE);
                    otpSendServiceManager.feedParamsWithoutKey(otpObj.toString());
                    otpSendServiceManager.featchData();
                    showSwitcher(loaderSwitcher, 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                    displaySnackBarMessage("OTP does not send pleas try again");
                }
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("TETETETETTETTETTTTTTTT", "onClick: ");
                navigateToSignIn();
            }
        });
        imgHome1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("TETETETETTETTETTTTTTTT", "onClick: ");
                navigateToSignIn();
            }
        });
        doneBtn.setOnClickListener(v -> {
            AppUtil.hideKeyboard(mobileNoEdt);
            if (otpCode.equals("")) {
                Toast.makeText(this, "Verification code cannot be blank", Toast.LENGTH_SHORT).show();
            }else {
                if (GlobalConf.checkInternetConnection(getApplicationContext())) {
                    try {
                        JSONObject stringCOnObj = new JSONObject();
                        stringCOnObj.put("user_id", userId);
                        stringCOnObj.put("otp_code", otpCode);
                        if (regJson != null) {
                            stringCOnObj.put("timestamp", System.currentTimeMillis());
                        }

                        otpVerifyServiceManager.prepareWebServiceJob();
                        otpVerifyServiceManager.feedParamsWithoutKey(stringCOnObj.toString());
                        otpVerifyServiceManager.featchData();
                        showSwitcher(loaderSwitcher, 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    displaySnackBarMessage(getResources().getString(R.string.internet_not_found_str));
                }
            }
        });

        mobileNoEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("0")) {
                    mobileNoEdt.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void navigateToSignIn() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private boolean validate() {
        if (!GlobalConf.checkInternetConnection(getApplicationContext())) {
            displaySnackBarMessage(getResources().getString(R.string.internet_not_found_str));
            return true;
        }
        if (AppUtil.isBlank(mobileNoEdt, "Mobile cannot be blank.")) return true;
        AppUtil.hideKeyboard(mobileNoEdt);
        return false;
    }


    public void displaySnackBarMessage(String message) {
        Snackbar.make(viewSwitcher, message, Snackbar.LENGTH_LONG).show();
    }


    public void showSwitcher(ViewSwitcher viewSwitcher, int index) {
        viewSwitcher.setDisplayedChild(index);
    }


    @Override
    public void onBackPressed() {
        if (viewSwitcher.getDisplayedChild() == 1) {
            openAlertDialog();
        } else {
            closeHome();
            super.onBackPressed();
        }
    }

    private void closeHome() {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent("close_home"));
    }

    private void openAlertDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.do_not_exit_str));
        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                closeHome();
            }
        });
        builder.show();
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {

    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        switch (serviceName) {
            case RegisterService.SERVICE_NAME -> {
                showSwitcher(loaderSwitcher, 0);
                if (registerServiceManager.getResponseMsg() != null) {

                    if (!registerServiceManager.getServiceStatus().toLowerCase().equals(SUCCESS)) {
                        DialogUtils.showInfoAlert(this, getString(R.string.app_name), registerServiceManager.getResponseMsg());
                    }


                    if (registerServiceManager.getUserId() != null) {
                        // add loader here
                        userId = registerServiceManager.getUserId();
                        otpSendServiceManager.prepareWebServiceJob();
                        JSONObject otpObj = new JSONObject();
                        try {
                            otpObj.put("user_id", registerServiceManager.getUserId());
                            otpObj.put("country_phone_code", "61");
                            otpObj.put("phone_number", mobileNoEdt.getText().toString().trim());
                            otpObj.put("otp_type", OTP_TYPE_PHONE);

                            otpSendServiceManager.feedParamsWithoutKey(otpObj.toString());
                            otpSendServiceManager.featchData();
                            showSwitcher(loaderSwitcher, 1);    //Show loading
                        } catch (JSONException e) {
                            e.printStackTrace();
                            displaySnackBarMessage("OTP does not send pleas try again");
                        }
                    }
                }
            }
            case OtpSendService.SERVICE_NAME -> onOTPSent();
            case OtpVerifyService.SERVICE_NAME -> {
                if (otpVerifyServiceManager.getServiceStatus() != null && !otpVerifyServiceManager.getServiceStatus().equals(" ")) {
                    if (otpVerifyServiceManager.getServiceStatus().toLowerCase().equals(SUCCESS)) {
                        onOTPVerified();
                    } else {
                        showSwitcher(loaderSwitcher, 0);
                        verifiCOdeEdt.setFocusable(true);
                        displaySnackBarMessage(otpVerifyServiceManager.getResponseMsg());
                    }
                } else {
                    showSwitcher(loaderSwitcher, 0);
                    verifiCOdeEdt.setFocusable(true);
                    displaySnackBarMessage(getString(R.string.pls_try_again_str));
                }
            }
            case EditProfileService.SERVICE_NAME -> {
                showSwitcher(loaderSwitcher, 0);
                updateSessionProfile();
                finish();
            }
        }
    }

    private void onOTPVerified() {
        if (screenFlow == OTP_VIEW) {
            Toast.makeText(this, R.string.otp_verfy_done, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//           /* Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);*/
//            finish();
            verifyEmailOtp();
        } else if (screenFlow == EDIT_PROFILE) {
            displaySnackBarMessage(getString(R.string.otp_verfy_done));
            editProfileAPiCalling();
        } else { //HOME

            User currentUser = sessionManager.getCurrentUser();
            currentUser.setOtpVerifiedStatus(VERIFIED);
            currentUser.setMobile(mobileNoEdt.getText().toString().trim());
            currentUser.setCountryCode("61");
            sessionManager.createOrUpdateLogin(currentUser);

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void onOTPSent() {
        showSwitcher(loaderSwitcher, 0); //hide loading
        otpSendServiceManager.getServiceStatus();

//        if (screenFlow == FROM_REGISTER) {
//            verifyEmailOtp();   //Open next email verify screen
//        } else {
            //show next mobile otp screen
            //For the VERIFY_MOBILE + EDIT_PROFILE + HOME
        screenFlow = OTP_VIEW;
            showSwitcher(viewSwitcher, 1);
            tvPhone.setText(getString(R.string.enter_confirm_code_str)+" ("+mobileNoEdt.getText().toString().trim()+")");
//        }
    }

    private void verifyEmailOtp() {
        try {
            JSONObject regJsonObj = new JSONObject(regJson);
            Intent intent = new Intent(getApplicationContext(), EmailOTPVerifyActivity.class);
            intent.putExtra(EXTRA_SCREEN_FLOW, EmailOTPVerifyActivity.ScreenFlow.REGISTER);
            intent.putExtra(EXTRA_USER_ID, userId);
            intent.putExtra(EXTRA_USER_EMAIL, regJsonObj.getString("email"));
            startActivity(intent);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateSessionProfile() {
        User currentUser = sessionManager.getCurrentUser();
        currentUser.setMobile(mobileNoEdt.getText().toString().trim());
        currentUser.setCountryCode("61");
        sessionManager.createOrUpdateLogin(currentUser);
    }

    private void editProfileAPiCalling() {
        JSONObject editProfile = new JSONObject();
        try {
            editProfile.put("customer_id", userId);
            editProfile.put("name", sessionManager.getCurrentUser().getUserId());
            editProfile.put("phone_number", mobileNoEdt.getText().toString().trim());
            editProfile.put("country_phone_code", "61");
            editProfileServiceManager.prepareWebServiceJob();
            editProfileServiceManager.feedParamsWithoutKey(editProfile.toString());
            editProfileServiceManager.featchData();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {
    }

    enum ScreeFlow {
        FROM_REGISTER, OTP_VIEW, FROM_HOME, EDIT_PROFILE
    }
}


