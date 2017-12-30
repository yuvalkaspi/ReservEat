package com.reserveat.reserveat.common.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.reserveat.reserveat.MainActivity;
import com.reserveat.reserveat.MatchedReservationActivity;
import com.reserveat.reserveat.R;


public class FBMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FBMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i(TAG, "From: " + remoteMessage.getFrom());

        String reservationId = null;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            reservationId = remoteMessage.getData().get("reservationId");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody() + "title: " + remoteMessage.getNotification().getTitle());
            sendNotification(remoteMessage, reservationId);
        }

    }

    private void sendNotification(RemoteMessage remoteMessage,String reservationId) {


        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Intent resultIntent = null;
        if(reservationId != null){
            resultIntent = new Intent(this, MatchedReservationActivity.class);
            resultIntent.putExtra("reservationId", reservationId);
        }
        else{
            resultIntent = new Intent(this, MainActivity.class);
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo_gris))
                .setSmallIcon(R.mipmap.reserveat_logo)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
