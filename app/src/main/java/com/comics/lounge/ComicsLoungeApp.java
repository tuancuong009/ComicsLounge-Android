package com.comics.lounge;

import android.app.Application;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.comics.lounge.activity.Home;
import com.comics.lounge.retrofit.RetroApi;
import com.comics.lounge.utils.DialogUtils;


import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;
import io.sentry.android.timber.SentryTimberIntegration;
import timber.log.Timber;

public class ComicsLoungeApp extends Application {

    public static DialogUtils dialogUtils;
    private static RetroApi retroApi;


    @Override
    public void onCreate() {
        super.onCreate();
        initDialog();

//        SentryAndroid.init(this, options -> {
//            Timber.plant(new Timber.DebugTree());
//
//            // default values:
//            // minEventLevel = ERROR
//            // minBreadcrumbLevel = INFO
//            options.addIntegration(
//                    new SentryTimberIntegration(
//                            SentryLevel.ERROR,
//                            SentryLevel.INFO
//                    )
//            );
//        });
        Intercom.initialize(this, "android_sdk-3251382576e0754ec9f6ab044012fba10372f005", "vw3iioae");

        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
        Intercom.client().setInAppMessageVisibility(Intercom.Visibility.VISIBLE);

    }

    private void initDialog() {
        dialogUtils = new DialogUtils();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        System.out.println("CLOSSSSSSSSSSSSSSSSSS");
    }

    public static RetroApi getRetroApi() {
        if (retroApi == null) {
            retroApi = RetroApi.Companion.create();
        }
        return retroApi;
    }

}
