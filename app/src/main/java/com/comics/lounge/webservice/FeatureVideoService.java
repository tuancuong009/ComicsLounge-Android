package com.comics.lounge.webservice;

import android.app.Activity;

import com.comics.lounge.modals.FeatureVideo;
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

public class FeatureVideoService extends CoreWebService implements Runnable {
    public static final String SERVICE_NAME = "FeatureVideoService";
    public Activity activity = null;
    private ServiceCallback serviceCallback = null;
    private ServiceStatus serviceStatus;
    private String responseMessage;
    private int responseCode;
    private JSONObject userObj;
    private String responseStatus;
    private List<FeatureVideo> featureVideoList = null;

    public FeatureVideoService(String url, ServiceCallback serviceCallback) {
        super(url);
        this.serviceCallback = serviceCallback;
        userObj = new JSONObject();
        featureVideoList = new LinkedList<FeatureVideo>();
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
            featureVideoList.addAll(gettingApiResponse(getLastResponse()));
            serviceCallback.serviceEnd(getLastResponse(), SERVICE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            serviceCallback.serviceEnd(BaseManager.ServiceStatus.UNKNOWN_HOST.getStatusMessage(), SERVICE_NAME);
        }
    }

    private List<FeatureVideo> gettingApiResponse(String lastResponse) {
        List<FeatureVideo> videos = new LinkedList<FeatureVideo>();
        try {
            JSONObject outerObj = new JSONObject(lastResponse);
            setResponseStatus(outerObj.getString("status"));
            if (!outerObj.isNull("videos")) {
                JSONArray vidArray = outerObj.getJSONArray("videos");
                for (int i = 0; i < vidArray.length(); i++) {
                    JSONObject videoObj = (JSONObject) vidArray.get(i);
                    FeatureVideo featureVideo = new FeatureVideo();
                    featureVideo.setId(videoObj.getInt("id"));
                    featureVideo.setName(videoObj.getString("video_name"));
                    featureVideo.setDescription(videoObj.getString("video_description"));
                    featureVideo.setImage(videoObj.getString("video_image"));
                    featureVideo.setLink(videoObj.getString("video_link"));
                    videos.add(featureVideo);
                }
            }
            this.serviceStatus = ServiceStatus.DATA_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            this.serviceStatus = ServiceStatus.DATA_FAILURE;
        }
        return videos;
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

    public List<FeatureVideo> getFeatureVideoList() {
        return featureVideoList;
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
