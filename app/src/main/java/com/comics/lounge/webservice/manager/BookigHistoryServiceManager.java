package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.BookingHistory;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.BookingHistoryService;

import java.util.List;


public class BookigHistoryServiceManager extends BaseManager implements ServiceCallback {
    String url = null;
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private BookingHistoryService bookingHistoryService = null;

    public BookigHistoryServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.bookingHistoryService.addParam(key, value);
    }

    public void generateUrl(String url) {
        this.url = url;
    }

    public void prepareWebServiceJob() {
        this.bookingHistoryService = new BookingHistoryService(url, this);
        this.bookingHistoryService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.bookingHistoryService);
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
        final BookingHistoryService.ServiceStatus serviceStatus = bookingHistoryService.getServiceStatus();
        if (serviceName.equals(BookingHistoryService.SERVICE_NAME)) {
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

    public void feedParamsWithoutKey(String value) {
        this.bookingHistoryService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return bookingHistoryService.getResponseStatus();
    }

    public List<BookingHistory> getBookingHistoryList() {
        return bookingHistoryService.getBookingHistoryList();
    }
}

