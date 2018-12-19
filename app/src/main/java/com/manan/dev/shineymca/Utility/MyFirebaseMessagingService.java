package com.manan.dev.shineymca.Utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.manan.dev.shineymca.RegisterSecondActivity;
import com.manan.dev.shineymca.RoundActivity;

import java.util.Map;

/**
 * Created by Suneja's on 12-07-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Map<String, String> payload = remoteMessage.getData();
            Log.v(TAG, "Message data payload: " + remoteMessage.getData());
            Intent intent = new Intent(this, RoundActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle(payload.get("title"))
                    .setContentText(payload.get("body"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(0, notificationBuilder.build());
        }

        if(remoteMessage.getNotification() != null){
            Log.v(TAG, "Message Notification Body: " + remoteMessage.getNotification());
        }
    }
}
