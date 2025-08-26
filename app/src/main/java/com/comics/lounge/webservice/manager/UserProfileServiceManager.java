package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.Wallet;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.UserProfileService;
import com.comics.lounge.webservice.WalletService;


public class UserProfileServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.USER_PROFILE;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private UserProfileService userProfileService = null;

    public UserProfileServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.userProfileService.addParam(key, value);
    }

    /*public void generateUrl(String url) {
        this.url = url;
    }*/


    public void prepareWebServiceJob() {
        this.userProfileService = new UserProfileService(url, this);
        this.userProfileService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.userProfileService);
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
        final UserProfileService.ServiceStatus serviceStatus = userProfileService.getServiceStatus();
        if (serviceName.equals(WalletService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public Wallet getWalletData() {
        return userProfileService.getWallet();
    }

    public void feedParamsWithoutKey(String value) {
        this.userProfileService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return userProfileService.getResponseStatus();
    }
}

