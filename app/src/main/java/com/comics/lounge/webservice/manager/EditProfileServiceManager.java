package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.EditProfileService;

import org.json.JSONObject;


public class EditProfileServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.EDIT_PROFILE;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private EditProfileService editProfileService = null;

    public EditProfileServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.editProfileService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.editProfileService = new EditProfileService(url, this);
        this.editProfileService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.editProfileService);
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
        final EditProfileService.ServiceStatus serviceStatus = editProfileService.getServiceStatus();
        if (serviceName.equals(EditProfileService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public String getResponseMsg() {
        return editProfileService.getResponseMessage();
    }

    public int getStatusCode() {
        return editProfileService.getResponseCode();
    }

    public JSONObject userObj() {
        return editProfileService.userObj();
    }

    public void feedParamsWithoutKey(String value) {
        this.editProfileService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return editProfileService.getResponseStatus();
    }

    public JSONObject getUserObj() {
        return editProfileService.userObj();
    }
}

