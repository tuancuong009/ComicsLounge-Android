package com.comics.lounge.webservice;

import android.app.Activity;

import com.comics.lounge.modals.Membership;
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

public class MembershipService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "MembershipService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;
    private List<Membership> memberships = null;

    public MembershipService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        memberships = new LinkedList<Membership>();
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
            memberships.addAll(gettingApiResponse(getLastResponse()));
            serviceCallback.serviceEnd(getLastResponse(), SERVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), SERVICE_NAME);
        }
    }

    private List<Membership> gettingApiResponse(String lastResponse) {
        List<Membership> member = new LinkedList<Membership>();
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            setResponseStatus(outerObj.getString("status"));
            if (!outerObj.isNull("memberships")) {
                JSONArray userObj = outerObj.getJSONArray("memberships");
                for (int i = 0; i < userObj.length(); i++) {
                    JSONObject eventObj = (JSONObject) userObj.get(i);
                    Membership membership = new Membership();
                    membership.setId(eventObj.getInt("membership_id"));
                    membership.setName(eventObj.getString("membershipname"));
                    membership.setDescription(eventObj.getString("description"));
                    membership.setPrice(eventObj.getString("price"));
                    membership.setOtherPrice(eventObj.getString("other_price"));
                    membership.setImage(eventObj.getString("image"));
                    membership.setDate(eventObj.getString("create_on"));
                    membership.setStatus(eventObj.getString("status"));
                    membership.setEvents(eventObj.getString("events"));
                    membership.setPermonthallowed(eventObj.getString("permonthallowed"));
                    member.add(membership);
                }
            }
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            this.serviceStatus = ServiceStatus.DATA_FAILURE;
        }
        return member;
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

    public List<Membership> getMemberships() {
        return memberships;
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
