package com.example.deliverywala.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.deliverywala.R;

public class OrderActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_NAME = "My Channel";

    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, HomeActivity.class);
            showNotification(
                OrderActivity.this,
                "Order Placed Successfully",
                "Thanks for Ordering with " + getResources().getString(R.string.app_name) + "!!"
            );
            startActivity(intent);
            finish();
        });
    }

    private void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = 
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_b)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}