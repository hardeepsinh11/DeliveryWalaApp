package com.example.deliverywala.base;

import android.app.Application;

public abstract class BaseViewModel extends RuntimePermissionViewModel {
    private final String TAG = BaseViewModel.class.getSimpleName();

    public BaseViewModel(Application application) {
        super(application);
    }

    /*public void generateFcmToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "getInstanceId failed " + task.getException());
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult().getToken();
                Log.d(TAG, "FirebaseMessagingService BaseViewModel: Token " + token);
                //prefs[PREF_FCM_TOKEN] = token;
                PreferenceHelper.saveFCMToken(StringUtils.nullSafe(token));
            });
    }*/
}