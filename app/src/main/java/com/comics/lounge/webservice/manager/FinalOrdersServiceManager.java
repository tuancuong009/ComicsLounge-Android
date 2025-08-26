package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.FinalOrdersService;

import org.json.JSONObject;

public class FinalOrdersServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.FINAL_ORDERS;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private FinalOrdersService finalOrdersService = null;

    public FinalOrdersServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.finalOrdersService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.finalOrdersService = new FinalOrdersService(url, this);
        this.finalOrdersService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.finalOrdersService);
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
        final FinalOrdersService.ServiceStatus serviceStatus = finalOrdersService.getServiceStatus();
        if (serviceName.equals(FinalOrdersService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public String getResponseMsg() {
        return finalOrdersService.getResponseMessage();
    }

    public int getStatusCode() {
        return finalOrdersService.getResponseCode();
    }

    public JSONObject userObj() {
        return finalOrdersService.userObj();
    }

    public void feedParamsWithoutKey(String value) {
        this.finalOrdersService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return finalOrdersService.getResponseStatus();
    }

    public JSONObject getUserObj() {
        return finalOrdersService.userObj();
    }
}

