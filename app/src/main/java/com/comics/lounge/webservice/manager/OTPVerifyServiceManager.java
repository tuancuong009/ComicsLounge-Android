package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.OtpVerifyService;

import org.json.JSONObject;


/**
 * Created by DIGITIZE on 13-Jun-17.
 */

public class OTPVerifyServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.OTP_VERIFY;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private OtpVerifyService otpVerifyService = null;

    public OTPVerifyServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.otpVerifyService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.otpVerifyService = new OtpVerifyService(url, this);
        this.otpVerifyService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.otpVerifyService);
            threadInstance.start();
        } else {
            serviceCallback.serviceEnd(ServiceStatus.NOT_REACHABLE.getStatusMessage(), "");
        }
    }

    @Override
    public void serviceStarted(final String msg, final String serviceName) {
        if (callingActivity != null) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceStarted(msg, serviceName));
        }
    }

    @Override
    public void serviceEnd(final String msg, final String serviceName) {
        final OtpVerifyService.ServiceStatus serviceStatus = otpVerifyService.getServiceStatus();
        if (serviceName.equals(OtpVerifyService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceCallback.serviceInProgress(msg, serviceName);
            }
        });
    }

    public String getResponseMsg() {
        return otpVerifyService.getResponseMessage();
    }

    public int getStatusCode() {
        return otpVerifyService.getResponseCode();
    }

    public JSONObject userObj() {
        return otpVerifyService.userObj();
    }

    public String getUserId() {
        return otpVerifyService.getUserId();
    }

    public void feedParamsWithoutKey(String value) {
        this.otpVerifyService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return otpVerifyService.getResponseStatus();
    }
}

