package com.comics.lounge.webservice;

import android.app.Activity;
import android.text.TextUtils;

import com.comics.lounge.modals.Wallet;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by DIGITIZE on 26-12-2017.
 */

public class UserProfileService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "UserProfileService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private String responseStatus;
    private Wallet wallet;

    public UserProfileService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        wallet = new Wallet();
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setContext(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        this.serviceStatus = ServiceStatus.DATA_FAILURE;
        serviceCallback.serviceStarted(SERVICE_NAME, SERVICE_NAME);
        try {
//            sendPostRequest();
            sendGetRequest();
            serviceCallback.serviceInProgress("", SERVICE_NAME);
            getResponse();
            gettingApiResponse(getLastResponse());
            serviceCallback.serviceEnd(getLastResponse(), SERVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), SERVICE_NAME);
        }
    }

    private void gettingApiResponse(String lastResponse) {
        Gson gson = new Gson();
        if (TextUtils.isEmpty(lastResponse)) return;

        wallet = gson.fromJson(lastResponse, Wallet.class);
        if (wallet.status != null) {//&& wallet.status.toLowerCase().equals("success")){
            setResponseStatus(wallet.status);
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;
        } else {
            setResponseStatus("failed");
            this.serviceStatus = ServiceStatus.DATA_FAILURE;
        }

        /*
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            setResponseStatus(outerObj.getString("status"));
            if (outerObj.has("access_event") && !outerObj.isNull("access_event")) {
                wallet.setAccessEvent(outerObj.getInt("access_event"));
                wallet.setEventCountAllowed(outerObj.has("event_count_allowed") && !outerObj.isNull("event_count_allowed") ? outerObj.getInt("event_count_allowed") : 0);
                wallet.setEventCountLeft(outerObj.has("event_count_left") && !outerObj.isNull("event_count_left") ? outerObj.getInt("event_count_left") : 0);
                wallet.setEventCancelPenlity(outerObj.has("event_cancel_penality") && outerObj.isNull("event_cancel_penality") ? outerObj.getInt("event_cancel_penality") : 0);
                wallet.setBalance(outerObj.has("balance") && outerObj.getString("balance") != null ? outerObj.getString("balance") : "0.0");
                wallet.setUserId(outerObj.has("user_id") && outerObj.isNull("user_id") ? outerObj.getString("user_id") : "0");
            }
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            this.serviceStatus = ServiceStatus.DATA_FAILURE;
        }*/
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseMessage) {
        this.responseStatus = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public enum ServiceStatus {
        DATA_SUCCESS(1000, "SUCCESS"),
        DATA_FAILURE(1001, "FAILURE");
        private int statucCode;
        private String statusMessage;

        ServiceStatus(int statucCode, String statusMessage) {
            this.statucCode = statucCode;
            this.statusMessage = statusMessage;
        }

        public String getStatusMessage() {
            return this.statusMessage;
        }

        public int getStatucCode() {
            return statucCode;
        }
    }
}
