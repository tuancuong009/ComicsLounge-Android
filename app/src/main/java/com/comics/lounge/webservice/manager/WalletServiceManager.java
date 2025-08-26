package com.comics.lounge.webservice.manager;

import android.app.Activity;

import com.comics.lounge.conf.GlobalConf;
import com.comics.lounge.modals.Wallet;
import com.comics.lounge.servicecallback.ServiceCallback;
import com.comics.lounge.webservice.BaseManager;
import com.comics.lounge.webservice.WalletService;


public class WalletServiceManager extends BaseManager implements ServiceCallback {
    String url = "";
    private ServiceCallback serviceCallback = null;
    private Thread threadInstance = null;
    private Activity callingActivity = null;
    private WalletService walletService = null;

    public WalletServiceManager(ServiceCallback serviceCallback, Activity callingActivity) {
        super(serviceCallback);
        this.serviceCallback = serviceCallback;
        this.callingActivity = callingActivity;
    }

    public void feedParams(String key, String value) {
        this.walletService.addParam(key, value);
    }

    public void generateUrl(String url) {
        this.url = url;
    }


    public void prepareWebServiceJob() {
        this.walletService = new WalletService(url, this);
        this.walletService.setContext(callingActivity);
    }

    public void featchData() {
        if (GlobalConf.checkInternetConnection(callingActivity)) {
            threadInstance = new Thread(this.walletService);
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
        final WalletService.ServiceStatus serviceStatus = walletService.getServiceStatus();
        if (serviceName.equals(WalletService.SERVICE_NAME)) {
            callingActivity.runOnUiThread(() -> serviceCallback.serviceEnd(serviceStatus.getStatusMessage(), serviceName));
        }
    }

    @Override
    public void serviceInProgress(final String msg, final String serviceName) {
        callingActivity.runOnUiThread(() -> serviceCallback.serviceInProgress(msg, serviceName));
    }

    public Wallet getWalletData() {
        return walletService.getWallet();
    }

    public void feedParamsWithoutKey(String value) {
        this.walletService.addParamWithoutKey(value);
    }

    public String getServiceStatus() {
        return walletService.getResponseStatus();
    }
}

