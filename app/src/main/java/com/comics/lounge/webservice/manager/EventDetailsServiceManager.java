package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.Event;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.EventDetailsService;

import java.util.Set;


public class EventDetailsServiceManager extends BaseManager implements ServiceCallback {
    String url = "";
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private EventDetailsService eventDetailsService = null;

    public EventDetailsServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void generateAPI(String url) {
        this.url = url;
    }

    public void feedParams(String key, String value) {
        this.eventDetailsService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.eventDetailsService = new EventDetailsService(url, this);
        this.eventDetailsService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.eventDetailsService);
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
        final EventDetailsService.ServiceStatus serviceStatus = eventDetailsService.getServiceStatus();
        if (serviceName.equals(EventDetailsService.SERVICE_NAME)) {
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


    public void feedParamsWithoutKey(String value) {
        this.eventDetailsService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return eventDetailsService.getResponseStatus();
    }

    public Event getEventObj() {
        return eventDetailsService.getEvent();
    }

    public Set<String> getDateList() {
        return eventDetailsService.getDateList();
    }

}

