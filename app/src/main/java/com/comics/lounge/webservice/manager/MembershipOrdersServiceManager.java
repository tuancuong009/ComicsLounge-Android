package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.MembershipOrdersService;

import org.json.JSONObject;


public class MembershipOrdersServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.MEMBERSHIP_PAYMENT;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private MembershipOrdersService membershipOrdersService = null;

    public MembershipOrdersServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.membershipOrdersService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.membershipOrdersService = new MembershipOrdersService(url, this);
        this.membershipOrdersService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.membershipOrdersService);
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
        final MembershipOrdersService.ServiceStatus serviceStatus = membershipOrdersService.getServiceStatus();
        if (serviceName.equals(MembershipOrdersService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public String getResponseMsg() {
        return membershipOrdersService.getResponseMessage();
    }

    public int getStatusCode() {
        return membershipOrdersService.getResponseCode();
    }

    public JSONObject userObj() {
        return membershipOrdersService.userObj();
    }

    public void feedParamsWithoutKey(String value) {
        this.membershipOrdersService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return membershipOrdersService.getResponseStatus();
    }

    public JSONObject getUserObj() {
        return membershipOrdersService.userObj();
    }
}

