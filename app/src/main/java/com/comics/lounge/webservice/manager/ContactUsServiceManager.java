package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.ContactUsService;

public class ContactUsServiceManager extends BaseManager implements ServiceCallback {
    String url = null;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private ContactUsService contactUsService = null;

    public ContactUsServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.contactUsService.addParam(key, value);
    }

    public void generateUrl(String url) {
        this.url = url;
    }

    public void prepareWebServiceJob() {
        this.contactUsService = new ContactUsService(url, this);
        this.contactUsService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.contactUsService);
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
        final ContactUsService.ServiceStatus serviceStatus = contactUsService.getServiceStatus();
        if (serviceName.equals(ContactUsService.SERVICE_NAME)) {
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
        return contactUsService.getResponseMessage();
    }

    public int getStatusCode() {
        return contactUsService.getResponseCode();
    }


    public String getServiceStatus() {
        return contactUsService.getResponseStatus();
    }
}

