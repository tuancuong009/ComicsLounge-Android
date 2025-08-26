package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.OtpSendService;

import org.json.JSONObject;


public class OTPSendServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.OTP_SEND;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private OtpSendService otpSendService = null;
    private OtpSendService.ServiceStatus serviceStatus = null;
    private String userId = null;
    private String message = null;
    private int statusCode;

    public OTPSendServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.otpSendService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.otpSendService = new OtpSendService(url, this);
        this.otpSendService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.otpSendService);
            threadInstance.start();
        } else {
            serviceCallback.serviceEnd(ServiceStatus.NOT_REACHABLE.getStatusMessage(), "");
        }
    }

    @Override
    public void serviceStarted(final String msg, final String serviceName) {
        if (callingActivity != null) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serviceCallback.serviceStarted(msg, serviceName);
                }
            });
        }
    }

    @Override
    public void serviceEnd(final String msg, final String serviceName) {
        final OtpSendService.ServiceStatus serviceStatus = otpSendService.getServiceStatus();
        if (serviceName.equals(OtpSendService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName);

                }
            });
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
        return otpSendService.getResponseMessage();
    }

    public int getStatusCode() {
        return otpSendService.getResponseCode();
    }

    public JSONObject userObj() {
        return otpSendService.userObj();
    }

    public String getUserId() {
        return otpSendService.getUserId();
    }

    public void feedParamsWithoutKey(String value) {
        this.otpSendService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return otpSendService.getResponseStatus();
    }
}

