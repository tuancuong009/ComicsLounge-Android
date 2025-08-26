package com.comics.lounge.utils;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.comics.lounge.R;
import com.comics.lounge.activity.Home;
import com.comics.lounge.activity.SplashScreenActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.intercom.android.sdk.push.IntercomPushClient;
import io.sentry.util.StringUtils;

public class FirebaseNotification extends FirebaseMessagingService {
    private final IntercomPushClient intercomPushClient = new IntercomPushClient();
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String, String> mapMessage = message.getData();
        if (intercomPushClient.isIntercomPush(mapMessage)) {
//            intercomPushClient.handlePush(getApplication(), mapMessage);
            Log.e("TAG", "onMessageReceived: "+mapMessage );
            String mess = mapMessage.get("body");
            int i = mess.indexOf(":");
            showArrivedNotification(mess.substring(0 , i), mess.substring(mess.indexOf(":") + 1));
        }
        if (message.getNotification() != null){
            showArrivedNotification(message.getNotification().getTitle(), message.getNotification().getBody());
        }
    }

    private void showArrivedNotification(String title, String body) {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.putExtra("intercom", "mess");
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "comicslounge_chanel")
                .setContentTitle(title)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                "comicslounge_chanel",
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);

        manager.createNotificationChannel(channel);
        manager.notify(101, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        intercomPushClient.sendTokenToIntercom(getApplication(), token);
    }
}
