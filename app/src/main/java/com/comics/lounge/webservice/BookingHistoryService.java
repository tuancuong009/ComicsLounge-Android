package com.comics.lounge.webservice;

import android.app.Activity;

import com.comics.lounge.modals.BookingHistory;
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

public class BookingHistoryService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "BookingHistoryService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;
    private List<BookingHistory> bookingHistoryList = null;

    public BookingHistoryService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        userObj = new JSONObject();
        bookingHistoryList = new LinkedList<BookingHistory>();
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
            bookingHistoryList.addAll(gettingApiResponse(getLastResponse()));
            serviceCallback.serviceEnd(getLastResponse(), SERVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), SERVICE_NAME);
        }
    }

    private List<BookingHistory> gettingApiResponse(String lastResponse) {
        List<BookingHistory> events = new LinkedList<BookingHistory>();
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            setResponseStatus(outerObj.getString("status"));
            if (!outerObj.isNull("orders")) {
                JSONArray userObj = outerObj.getJSONArray("orders");
                for (int i = 0; i < userObj.length(); i++) {
                    JSONObject eventObj = (JSONObject) userObj.get(i);
                    JSONArray itemArray = eventObj.getJSONArray("items");
                    if (itemArray.length() > 0) {
                        JSONObject itemObj = (JSONObject) itemArray.get(0);
                        BookingHistory bookingHistory = new BookingHistory();
                        bookingHistory.setProductId(itemObj.getInt("product_id"));
                        bookingHistory.setOrderEventId(itemObj.getInt("order_event_id"));
                        bookingHistory.setVirtuemartOrderId(itemObj.getInt("virtuemart_order_id"));
                        bookingHistory.setProductName(itemObj.getString("product_name"));
                        bookingHistory.setProductType(itemObj.getString("product_type"));
                        bookingHistory.setPrice(itemObj.getDouble("price"));
                        bookingHistory.setProductStatus(itemObj.getString("product_status"));
                        bookingHistory.setOpenTime(itemObj.getString("opentime"));
                        bookingHistory.setShowTime(itemObj.getString("showtime"));
                        bookingHistory.setDinnreTime(itemObj.getString("dinnertime"));
                        bookingHistory.setPerformerName(itemObj.getString("performerName"));
                        bookingHistory.setSupporterName(itemObj.getString("SupporterName"));
                        bookingHistory.setImage(itemObj.getString("image"));
                        bookingHistory.setEventDate(itemObj.getString("event_date"));
                        bookingHistory.setShowType(itemObj.getString("show_type"));
                        events.add(bookingHistory);
                    }

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

    public List<BookingHistory> getBookingHistoryList() {
        return bookingHistoryList;
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
