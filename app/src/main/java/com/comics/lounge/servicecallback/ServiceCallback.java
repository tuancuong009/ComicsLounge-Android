package com.comics.lounge.servicecallback;

/**
 * Created by GAURAV on 07-Apr-16.
 */
public interface ServiceCallback {
    void serviceStarted(String msg, String serviceName);

    void serviceEnd(String msg, String serviceName);

    void serviceInProgress(String msg, String serviceName);
}
