package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.Event;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.EventService;
import com.comics.lounge.webservice.PopUpService;

import java.util.List;


public class PopUpServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.POP_UP_URL;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private PopUpService popUpService = null;

    public PopUpServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.popUpService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.popUpService = new PopUpService(url, this);
        this.popUpService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.popUpService);
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
        final PopUpService.ServiceStatus serviceStatus = popUpService.getServiceStatus();
        if (serviceName.equals(PopUpService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }


    public String getServiceStatus() {
        return popUpService.getResponseStatus();
    }
}

