package com.comics.lounge.conf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

import com.comics.lounge.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

/**
 * Created by GAURAV on 26-12-19.
 */

public class GlobalConf {
    /**
     * @param context
     * @return check internet connection
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean checkInternetConnection(Context context, View view) {
        if (!checkInternetConnection(context)) {
            Snackbar.make(view, R.string.working_internet_connection, Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static void showServerError(View view) {
        Snackbar.make(view, R.string.server_error_, Snackbar.LENGTH_LONG).show();
    }

    public static String getUniqueID() {
        return UUID.randomUUID().toString();
    }

}
