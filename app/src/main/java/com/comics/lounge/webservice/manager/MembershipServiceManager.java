package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.conf.UrlCollection;
import com.comics.lounge.modals.Membership;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.MembershipService;

import java.util.List;


public class MembershipServiceManager extends BaseManager implements ServiceCallback {
    String url = UrlCollection.MEMBERSHIP;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private MembershipService membershipService = null;

    public MembershipServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.membershipService.addParam(key, value);
    }

    public void prepareWebServiceJob() {
        this.membershipService = new MembershipService(url, this);
        this.membershipService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.membershipService);
            threadInstance.start();
        } else {
            serviceCallback.serviceEnd(ServiceStatus.NOT_REACHABLE.getStatusMessage(), "");
        }
    }

    @Override
    public void serviceStarted(final String msg, final String serviceName) {
        if (callingActivity != null) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serviceCallback.serviceStarted(msg, serviceName);
                }
            });
        }
    }

    @Override
    public void serviceEnd(final String msg, final String serviceName) {
        final MembershipService.ServiceStatus serviceStatus = membershipService.getServiceStatus();
        if (serviceName.equals(MembershipService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName);

                }
            });
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serviceCallback.serviceInProgress(msg, serviceName);
            }
        });
    }

    public List<Membership> getMemberships() {
        return membershipService.getMemberships();
    }

    public void feedParamsWithoutKey(String value) {
        this.membershipService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return membershipService.getResponseStatus();
    }
}

