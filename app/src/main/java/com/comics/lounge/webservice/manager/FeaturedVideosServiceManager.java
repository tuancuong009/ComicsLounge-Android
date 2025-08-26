package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.FeatureVideo;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.FeatureVideoService;

import java.util.List;

public class FeaturedVideosServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.VIDEO_LIST;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private FeatureVideoService featureVideoService = null;

    public FeaturedVideosServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.featureVideoService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.featureVideoService = new FeatureVideoService(url, this);
        this.featureVideoService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.featureVideoService);
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
        final FeatureVideoService.ServiceStatus serviceStatus = featureVideoService.getServiceStatus();
        if (serviceName.equals(FeatureVideoService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public List<FeatureVideo> getVideoList() {
        return featureVideoService.getFeatureVideoList();
    }

    public void feedParamsWithoutKey(String value) {
        this.featureVideoService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return featureVideoService.getResponseStatus();
    }
}

