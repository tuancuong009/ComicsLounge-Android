package com.comics.lounge.webservice;

import android.app.Activity;
import android.util.Log;

import com.comics.lounge.modals.Date;
import com.comics.lounge.modals.Event;
import com.comics.lounge.modals.EventPriceDates;
import com.comics.lounge.servicecallback.ServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by DIGITIZE on 26-12-2017.
 */

public class EventDetailsService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "EventDetailsService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;
    private Event event;
    private Set<String> dateList = null;

    public EventDetailsService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        userObj = new JSONObject();
        event = new Event();
        dateList = new HashSet<String>();

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
            //sendPostRequest();
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
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            setResponseStatus(outerObj.getString("status"));
            if (!outerObj.isNull("info")) {
                JSONObject infoObj = outerObj.getJSONObject("info");
                event.setDisablestatus(outerObj.getBoolean("disable_status"));
                event.setDisableStatusMsg(outerObj.getString("disable_status_msg"));
                event.setId(infoObj.getInt("product_id"));
                event.setLink(infoObj.getString("link"));
                event.setProductName(infoObj.getString("product_name"));
                event.setPerformerName(infoObj.getString("performer_name"));
                event.setFav(infoObj.optBoolean("isFav"));
                event.setSupprterName(infoObj.getString("supporter_name"));
                event.setProductDesc(infoObj.getString("product_desc"));
                event.setShowTime(infoObj.getString("showtime"));
                event.setDinnerTime(infoObj.getString("dinnertime"));
                event.setOpenTime(infoObj.getString("opentime"));
                event.setImage(infoObj.getString("image"));
                event.setTotalFreeEventLeft(infoObj.isNull("total_free_event_left") ? 0:  infoObj.getInt("total_free_event_left"));
                event.setFree(infoObj.isNull("free") ? 0 : infoObj.getInt("free"));
                event.setMaxOrder(infoObj.isNull("max_order") ? 0 : infoObj.getInt("max_order"));
            }

            if(!outerObj.isNull("dates")){
                JSONArray datesArray = outerObj.getJSONArray("dates");
                for (int j = 0; j < datesArray.length(); j++) {
                    JSONObject jsonObject = (JSONObject) datesArray.get(j);
                    Date date = new Date();
                    date.setDate(jsonObject.getString("date"));
                    date.setQty(jsonObject.getInt("qty"));
                    event.addDateObj(date);
                }
            }

            if (!outerObj.isNull("serverTime")) {
                event.setServerTime(outerObj.getString("serverTime"));
            }
            if (!outerObj.isNull("event_closedateTime")) {
                event.setEventCloseTime(outerObj.getString("event_closedateTime"));
            }

            event.sessionsAllowed = outerObj.getString("sessions_allowed");
//            event.setMemberAccess(outerObj.getBoolean("memberAccess"));
//            Log.e("TAG", "gettingApiResponse: "+outerObj.getBoolean("memberAccess") );

            if (!outerObj.isNull("sessions")) {
                JSONArray sessionInfo = outerObj.getJSONArray("sessions");
                for (int j = 0; j < sessionInfo.length(); j++) {
                    String sessionOBj = sessionInfo.getString(j);
                    event.setSessionList(sessionOBj);
                }
            }

            if (outerObj.has("sessions_open")) {
                if (!outerObj.isNull("sessions_open")) {
                    JSONArray sessionOpen = outerObj.getJSONArray("sessions_open");
                    for (int j = 0; j < sessionOpen.length(); j++) {
                        if (!sessionOpen.getString(j).equals("null")) {
                            String sessionOBj = sessionOpen.getString(j);
                            event.addSessionOpen(sessionOBj);
                        }

                    }
                }
            }

            if (outerObj.has("sessions_dinner")) {
                if (!outerObj.isNull("sessions_dinner")) {
                    JSONArray sessionDinner = outerObj.getJSONArray("sessions_dinner");
                    for (int j = 0; j < sessionDinner.length(); j++) {
                        if (!sessionDinner.getString(j).equals("null")) {
                            String sessionOBj = sessionDinner.getString(j);
                            event.addDinnerList(sessionOBj);
                        }
                    }
                }
            }

            if (!outerObj.isNull("price_dates")) {
                JSONArray infoPrice = outerObj.getJSONArray("price_dates");

                for (int j = 0; j < infoPrice.length(); j++) {
                    JSONObject priceInfo = infoPrice.getJSONObject(j);
                    String dates = priceInfo.getString("date");
                    EventPriceDates eventPriceDates = new EventPriceDates();
                    eventPriceDates.setId(priceInfo.getInt("price_id"));
                    eventPriceDates.setDate(priceInfo.getString("date"));
                    eventPriceDates.setText(priceInfo.getString("text"));
                    eventPriceDates.setDisplay(!priceInfo.isNull("display") ? priceInfo.getString("display") : "");
                    eventPriceDates.setFreeCount(priceInfo.getInt("free_count"));
                    eventPriceDates.setPrice(priceInfo.getString("price"));
                    eventPriceDates.setAttributeJson(priceInfo.getJSONObject("attribute"));

                    if (priceInfo.has("attribute_session_first")) {
                        eventPriceDates.setAttributeFirstJson(priceInfo.getJSONObject("attribute_session_first"));
                    }
                    if (priceInfo.has("attribute_session_second")) {
                        eventPriceDates.setAttributeSecondJson(priceInfo.getJSONObject("attribute_session_second"));
                    }

                    eventPriceDates.setShowOnlytickets(!priceInfo.isNull("showonly_tickets") ? priceInfo.getString("showonly_tickets") : priceInfo.getString("showwithmeal_tickets"));
                    event.addEventPrice(eventPriceDates);
                    dateList.add(dates);
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

    public Event getEvent() {
        return event;
    }

    public Set<String> getDateList() {
        return dateList;
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
