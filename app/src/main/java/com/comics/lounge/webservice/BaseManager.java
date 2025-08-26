package com.comics.lounge.webservice;


import com.comics.lounge.servicecallback.ServiceCallback;

/**
 * Created by Gaurav
 */
public class BaseManager implements ServiceCallback {
    protected ServiceCallback serviceCallback = null;
    private String serviceName;

    public BaseManager(ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
    }

    @Override
    public void serviceStarted(String msg, String serviceName) {
        this.serviceCallback.serviceStarted(msg, serviceName);
    }

    @Override
    public void serviceEnd(String msg, String serviceName) {
        this.serviceCallback.serviceEnd(msg, serviceName);
    }

    @Override
    public void serviceInProgress(String msg, String serviceName) {
        this.serviceCallback.serviceEnd(msg, serviceName);
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public enum ServiceStatus {
        NOT_REACHABLE(101, "NOT REACHABLE"),
        NEGATIVE_RESPONSE(102, "NEGATIVE RESPONSE"),
        POSITIVE_RESPONSE(103, "POSITIVE RESPONSE"),
        UNKNOWN_HOST(104, "UNKNOWN_HOST");
        private int statusCode;
        private String statusMessage;

        ServiceStatus(int statusCode, String statusMessage) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
        }

        public String getStatusMessage() {
            return this.statusMessage;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

}
