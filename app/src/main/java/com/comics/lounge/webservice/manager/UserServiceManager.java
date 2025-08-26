package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.UserService;

import org.json.JSONObject;


public class UserServiceManager extends BaseManager implements ServiceCallback {
    String url = "";
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private UserService loginService = null;


    public UserServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.loginService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.loginService = new UserService(url, this);
        this.loginService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.loginService);
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
        final UserService.ServiceStatus serviceStatus = loginService.getServiceStatus();
        if (serviceName.equals(UserService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public String getResponseMsg() {
        return loginService.getResponseMessage();
    }

    public int getStatusCode() {
        return loginService.getResponseCode();
    }

    public JSONObject userObj() {
        return loginService.userObj();
    }

    public void feedParamsWithoutKey(String value) {
        this.loginService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return loginService.getResponseStatus();
    }

    public JSONObject getUserObj() {
        return loginService.userObj();
    }

    public void generateUrl(String url) {
        this.url = url;
    }
}

