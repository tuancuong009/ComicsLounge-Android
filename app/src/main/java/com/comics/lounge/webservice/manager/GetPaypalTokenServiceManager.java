package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.GetPaypalTokenService;


/**
 * Created by DIGITIZE on 13-Jun-17.
 */

public class GetPaypalTokenServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.GET_PAYPAL_TOKEN;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private GetPaypalTokenService calenderService = null;

    public GetPaypalTokenServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.calenderService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.calenderService = new GetPaypalTokenService(url, this);
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
            callingActivity.runOnUiThread(() -> serviceCallback.serviceStarted(msg, serviceName));
        }
    }

    @Override
    public void serviceEnd(final String msg, final String serviceName) {
        final GetPaypalTokenService.ServiceStatus serviceStatus = calenderService.getServiceStatus();
        if (serviceName.equals(GetPaypalTokenService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }


    public void feedParamsWithoutKey(String value) {
        this.calenderService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return calenderService.getResponseStatus();
    }

    public String getPaypalToken() {
        return calenderService.getPaypalToken();
    }

}

