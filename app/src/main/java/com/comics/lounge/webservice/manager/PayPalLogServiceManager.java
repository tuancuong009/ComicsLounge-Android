package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.PayPalLogService;
import com.comics.lounge.webservice.RegisterService;

import org.json.JSONObject;


/**
 * Created by DIGITIZE on 13-Jun-17.
 */

public class PayPalLogServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.PAYPAL_LOG;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private PayPalLogService payPalLogService = null;

    public PayPalLogServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.payPalLogService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.payPalLogService = new PayPalLogService(url, this);
        this.payPalLogService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.payPalLogService);
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
        final PayPalLogService.ServiceStatus serviceStatus = payPalLogService.getServiceStatus();
        if (serviceName.equals(RegisterService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public String getResponseMsg() {
        return payPalLogService.getResponseMessage();
    }

    public int getStatusCode() {
        return payPalLogService.getResponseCode();
    }

    public JSONObject userObj() {
        return payPalLogService.userObj();
    }

    public String getUserId() {
        return payPalLogService.getUserId();
    }

    public void feedParamsWithoutKey(String value) {
        this.payPalLogService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return payPalLogService.getResponseStatus();
    }
}

