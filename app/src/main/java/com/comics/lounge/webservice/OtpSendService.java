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

public class OtpSendService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "OtpSendService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;
    private String userId;

    public OtpSendService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        userObj = new JSONObject();
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
            Log.e("OTP: ", getLastResponse());
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

    //{"code":1000,"status":"success","message":"Login Successfully","data":{"id":"13","role_id":"2","udid":"895cd201939eb1d","device_id":"000000000000000","name":"gaurav maru","email":"gaurav.maru@digitize-info.com","mobile":"8000101178","password_hash":"e10adc3949ba59abbe56e057f20f883e","password_reset_token":null,"username":"gaurav","profile_pic":null,"city":"10856","activation_token":"","is_active":"0","created_at":"1517467594","updated_at":"0"}}
    private void extractCategory(String lastResponse) {
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            if (outerObj.has("message")) {
                setResponseMessage(outerObj.getString("message"));
            }


            setResponseStatus(outerObj.getString("status"));
            // setUserId(outerObj.getString("user_id"));
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;


        } catch (JSONException e) {
            e.printStackTrace();
            this.serviceStatus = ServiceStatus.DATA_FAILURE;
        }
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
