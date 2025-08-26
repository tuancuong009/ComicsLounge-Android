package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.DeleteTicketService;

import org.json.JSONObject;

public class DeleteTicketServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.DELETE_EVENT;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private DeleteTicketService deleteTicketService = null;

    public DeleteTicketServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.deleteTicketService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.deleteTicketService = new DeleteTicketService(url, this);
        this.deleteTicketService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.deleteTicketService);
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
        final DeleteTicketService.ServiceStatus serviceStatus = deleteTicketService.getServiceStatus();
        if (serviceName.equals(DeleteTicketService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public String getResponseMsg() {
        return deleteTicketService.getResponseMessage();
    }

    public int getStatusCode() {
        return deleteTicketService.getResponseCode();
    }

    public JSONObject userObj() {
        return deleteTicketService.userObj();
    }

    public void feedParamsWithoutKey(String value) {
        this.deleteTicketService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return deleteTicketService.getResponseStatus();
    }

    public JSONObject getUserObj() {
        return deleteTicketService.userObj();
    }
}

