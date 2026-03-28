package com.example.deliverywala.firebase_notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.deliverywala.R;
import com.example.deliverywala.activities.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Intent intent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            sendNotification(remoteMessage);
        }else{
            sendNotification(remoteMessage);
        }*/
        intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.e("message", remoteMessage.getData().get("name"));

        InputStream imgUrl = null;
        try {
            imgUrl = new URL("https://firebasestorage.googleapis.com/v0/b/fir-db-bafc7.appspot.com/o/banner_notis.jpg?alt=media&token=ea178495-ca5e-46eb-8cb0-60be3974f8fa").openStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(imgUrl);
            /*bmp = BitmapFactory.decodeResource(
                applicationContext.getResources(),
                R.drawable.banner_notis
            )*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String channelId = getResources().getString(R.string.notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.b_white)
                .setContentTitle(remoteMessage.getData().get("name"))
                .setContentText(remoteMessage.getData().get("age"))
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp))
                .setLargeIcon(getBitmapfromUrl(imgUrl.toString()))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }

    @Override
    public void handleIntent(Intent intent) {
        try {
            if (intent != null && intent.getExtras() != null) {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("https://firebasestorage.googleapis.com/v0/b/fir-db-bafc7.appspot.com/o/banner_notis.jpg?alt=media&token=ea178495-ca5e-46eb-8cb0-60be3974f8fa");
                for (String key : intent.getExtras().keySet()) {
                    builder.addData(key, intent.getExtras().getString(key));
                }
                onMessageReceived(builder.build());
            } else {
                super.handleIntent(intent);
            }
        } catch (Exception e) {
            super.handleIntent(intent);
        }
    }

    private Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /*
    @SuppressLint("LongLogTag")
    private void sendNotification(RemoteMessage remoteMessage) {
        String channelId = getResources().getString(R.string.notification_channel_id);

        if (!isInBackground) {
            //foreground app
            Log.e("remoteMessage foreground", remoteMessage.getData().toString());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                getApplicationContext(), channelId
            );
            notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.b_white)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setNumber(10)
                .setTicker("Bestmarts")
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
            notificationManager.notify(1, notificationBuilder.build());
        } else {
            Log.e("remoteMessage background", remoteMessage.getData().toString());
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String body = data.get("body");
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            );
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                getApplicationContext(), channelId
            );
            notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setNumber(10)
                .setTicker("Bestmarts")
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
            notificationManager.notify(1, notificationBuilder.build());
        }
    }
    */
}