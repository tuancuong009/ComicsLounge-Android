package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.ConfirmTicket;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.ConfirmTickertService;

import java.util.List;


/**
 * Created by DIGITIZE on 13-Jun-17.
 */

public class ConfirmTickertServiceManager extends BaseManager implements ServiceCallback {
    String url = null;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private ConfirmTickertService confirmTickertService = null;

    public ConfirmTickertServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.confirmTickertService.addParam(key, value);
    }

    public void generateUrl(String url) {
        this.url = url;
    }

    public void prepareWebServiceJob() {
        this.confirmTickertService = new ConfirmTickertService(url, this);
        this.confirmTickertService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.confirmTickertService);
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
        final ConfirmTickertService.ServiceStatus serviceStatus = confirmTickertService.getServiceStatus();
        if (serviceName.equals(ConfirmTickertService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public void feedParamsWithoutKey(String value) {
        this.confirmTickertService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return confirmTickertService.getResponseStatus();
    }

    public List<ConfirmTicket> getConfirmTickets() {
        return confirmTickertService.getConfirmTicketList();
    }
}

