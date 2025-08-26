package com.comics.lounge.webservice;

import android.app.Activity;

import com.comics.lounge.utils.DatesUtils;
import com.comics.lounge.modals.Event;
import com.comics.lounge.servicecallback.ServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalenderService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "CalenderService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;
    private Map<Date, List<Event>> caDateListMap = null;

    public CalenderService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        userObj = new JSONObject();
        caDateListMap = new HashMap<Date, List<Event>>();
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
            gettingApiResponse(getLastResponse());
            serviceCallback.serviceEnd(getLastResponse(), SERVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), SERVICE_NAME);
        }
    }

    private void gettingApiResponse(String lastResponse) {
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            setResponseStatus(outerObj.getString("status"));
            if (!outerObj.isNull("products")) {
                JSONArray userObj = outerObj.getJSONArray("products");
                if (userObj.length() > 0) {
                    String startDate = userObj.getJSONObject(0).getString("start_date");
                    String lastDate = userObj.getJSONObject(userObj.length() - 1).getString("end_date");

                    List<Date> dates = DatesUtils.getDates(startDate, lastDate);
                    for (Date date : dates) {
                        List<Event> calenderModalList = new ArrayList<Event>();
                        for (int i = 0; i < userObj.length(); i++) {
                            JSONObject eventObj = (JSONObject) userObj.get(i);
                            Event event = new Event();
                            boolean isBol = DatesUtils.isBetweenCurrntMonth(eventObj.getString("start_date"),
                                    eventObj.getString("end_date"), date);
                            if (isBol) {
                                event.setProductName(eventObj.getString("product_name"));
                                event.setLink(eventObj.getString("link"));
                                event.setProductDesc(eventObj.getString("product_s_desc"));
                                event.setStartDate(eventObj.getString("start_date"));
                                event.setEndDate(eventObj.getString("end_date"));
                                event.setOpenTime(eventObj.getString("open_time"));
                                event.setDinnerTime(eventObj.getString("dinnertime"));
                                event.setImage(eventObj.getString("img"));
                                event.setDateStr(eventObj.getString("date"));
                                event.setId(eventObj.getInt("product_id"));
                                event.setShowTime(eventObj.getString("showtime"));
                                calenderModalList.add(event);
                            } else {

                            }
                        }
                        if (calenderModalList.size() > 0) {
                            caDateListMap.put(date, calenderModalList);
                        }
                    }
                }
            }
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            this.serviceStatus = ServiceStatus.DATA_FAILURE;
        }

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

    public Map<Date, List<Event>> getCaDateListMap() {
        return caDateListMap;
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
