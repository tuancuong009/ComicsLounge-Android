package com.comics.lounge.webservice;

import android.app.Activity;

import com.comics.lounge.modals.Event;
import com.comics.lounge.servicecallback.ServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by DIGITIZE on 26-12-2017.
 */

public class EventService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "EventService";
    public static final String PARAM_USERNAME = "";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_DEVICE_TYPE = "device_type";
    public static final String PARAM_DEVICEID = "device_id";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;
    private List<Event> eventList = null;

    public EventService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        userObj = new JSONObject();
        eventList = new LinkedList<Event>();
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
            eventList.addAll(gettingApiResponse(getLastResponse()));
            serviceCallback.serviceEnd(getLastResponse(), SERVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), SERVICE_NAME);
        }
    }

    private List<Event> gettingApiResponse(String lastResponse) {
        List<Event> events = new LinkedList<Event>();
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            setResponseStatus(outerObj.getString("status"));
            if (!outerObj.isNull("products")) {
                JSONArray userObj = outerObj.getJSONArray("products");
                for (int i = 0; i < userObj.length(); i++) {
                    JSONObject eventObj = (JSONObject) userObj.get(i);
                    Event event = new Event();
                    event.setId(eventObj.getInt("product_id"));
                    event.setProductName(eventObj.getString("product_name"));
                    event.setImage(eventObj.getString("img"));
                    event.setProductDesc(eventObj.getString("product_s_desc"));
                    event.setLink(eventObj.getString("link"));
                    event.setDate(eventObj.getString("date"));
                    events.add(event);
                }
            }
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;


        } catch (JSONException e) {
            e.printStackTrace();
            this.serviceStatus = ServiceStatus.DATA_FAILURE;
        }
        return events;
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

    public List<Event> getAllEventList() {
        return eventList;
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
