package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.Event;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.CalenderService;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class CalenderServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.CALENDER;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private CalenderService calenderService = null;

    public CalenderServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.calenderService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.calenderService = new CalenderService(url, this);
        this.calenderService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.calenderService);
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
        final CalenderService.ServiceStatus serviceStatus = calenderService.getServiceStatus();
        if (serviceName.equals(CalenderService.SERVICE_NAME)) {
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
        this.calenderService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return calenderService.getResponseStatus();
    }

    public Map<Date, List<Event>> getCalMapList() {
        return calenderService.getCaDateListMap();
    }
}

