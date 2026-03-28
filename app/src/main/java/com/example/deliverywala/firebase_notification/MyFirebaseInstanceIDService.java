package com.example.deliverywala.firebase_notification;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        // String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Log.d(TAG, "Refreshed token: " + refreshedToken);
        super.onNewToken(s);
    }

    private static final String TAG = "MyFirebaseIIDService";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
}