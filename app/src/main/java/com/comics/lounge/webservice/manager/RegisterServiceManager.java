package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.RegisterService;

import org.json.JSONObject;


/**
 * Created by DIGITIZE on 13-Jun-17.
 */

public class RegisterServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.REGISTER;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private RegisterService registerService = null;

    public RegisterServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.registerService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.registerService = new RegisterService(url, this);
        this.registerService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.registerService);
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
        final RegisterService.ServiceStatus serviceStatus = registerService.getServiceStatus();
        if (serviceName.equals(RegisterService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public String getResponseMsg() {
        return registerService.getResponseMessage();
    }

    public int getStatusCode() {
        return registerService.getResponseCode();
    }

    public JSONObject userObj() {
        return registerService.userObj();
    }

    public String getUserId() {
        return registerService.getUserId();
    }

    public void feedParamsWithoutKey(String value) {
        this.registerService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return registerService.getResponseStatus();
    }
}

