package com.comics.lounge.webservice;

import android.app.Activity;
import android.util.Log;

import com.comics.lounge.servicecallback.ServiceCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by DIGITIZE on 26-12-2017.
 */

public class MembershipOrdersService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "MembershipOrdersService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;

    public MembershipOrdersService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        userObj = new JSONObject();
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
            sendPostRequest();
            //sendGetRequest();
            serviceCallback.serviceInProgress("", SERVICE_NAME);
            getResponse();
            Log.e("MemberSHip ", getLastResponse());
            extractCategory(getLastResponse());
            serviceCallback.serviceEnd(getLastResponse(), SERVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), SERVICE_NAME);
        }
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
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

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    //{    "status": "success",    "paymentId": null,    "message": "you successfully purchased the Membership"}
    private void extractCategory(String lastResponse) {
        if (lastResponse != null && !lastResponse.equals("")) {
            try {
                JSONObject outerObj = new JSONObject(lastResponse);
                if (outerObj.has("message")) {
                    if (!outerObj.isNull("message")) {
                        setResponseMessage(outerObj.getString("message"));
                    }
                }
                if (outerObj.has("status")) {
                    if (!outerObj.isNull("status")) {
                        setResponseStatus(outerObj.getString("status"));
                    }
                }

             /*   if(!outerObj.isNull("paymentId")){

                }else{
                    setResponseStatus("Payment failed try again");
                }*/

                /*if (!outerObj.isNull("status") && outerObj.getString("status").equals("success")){
                    setResponseStatus(outerObj.getString("status"));
                }*/

                this.serviceStatus = ServiceStatus.DATA_SUCCESS;


            } catch (JSONException e) {
                e.printStackTrace();
                this.serviceStatus = ServiceStatus.DATA_FAILURE;
            }
        } else {
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;
        }

    }

    public void setUserObj(JSONObject userObj) {
        this.userObj = userObj;
    }

    public JSONObject userObj() {
        return userObj;
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
