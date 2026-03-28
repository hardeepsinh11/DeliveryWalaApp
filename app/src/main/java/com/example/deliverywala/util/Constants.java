package com.example.deliverywala.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.example.deliverywala.model.ScreenDetails;
import com.example.deliverywala.MyApplication;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Constants {
    public static final String admin = "admin@admin.com";
    public static final String CommonURL = "https://believe-the-music-academy.business.site/";
    public static final String SERVER_KEY = "AAAAwzMznv4:APA91bGgSjmD5pFbgMwhWnG5hilS-bEq8X3K3MWRdPsRJIlgf6vunz1HI-DlbvJghq7BP289E_VJvZLJN9mVkjkQfBS2sNg2Q144kEgfKqLG3yOgUDMTyWbtSiJfieSfnKLWmIdJAUDk";
    public static String OBJ_SEC = "";
    public static final String[] orderStatus = {"0", "1", "2"};
    public static String Emp_contact = "";
    public static String Emp_email = "";
    public static String UserType = "";
    public static String MerchantID = "uvNect17196722689902";
    public static String MerchantKey = "eD#xCCeXa2Oil37E";
    public static String TOKENIS = "";
    public static SessionManager sessionmgr = null;
    public static String DBItemName = "FoodItems";
    public static String DBItemOrderName = "FoodOrderDetails";


    public static SessionManager session(Context context) {
        if (sessionmgr == null) {
            sessionmgr = new SessionManager(context);
        }
        return sessionmgr;
    }

    public static void AddFirebaseScreen(int screenId, String screenName, Context mContext) {
        try {
            Log.e("screen_name", screenName);
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
            ScreenDetails screenDetails = new ScreenDetails();
            screenDetails.setId(screenId);
            screenDetails.setName(screenName);
            Bundle bundle = new Bundle();
            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, screenDetails.getId());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, screenDetails.getName());
            bundle.putString(FirebaseAnalytics.Event.LOGIN, Emp_contact);
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenDetails.getName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
            mFirebaseAnalytics.setUserId(String.valueOf(screenDetails.getId()));
            mFirebaseAnalytics.setUserProperty("screen_name", screenDetails.getName());
            try {
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
            } catch (Exception e) {
                Log.e("login_Firebase_error1", e.toString());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("login_Firebase_error", e.toString());
        }
    }

    public static boolean isNetworkAvailable(Context ctx) {
        boolean hasInternet;
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network networkCapabilities = connectivityManager.getActiveNetwork();
            if (networkCapabilities == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(networkCapabilities);
            if (actNw == null) return false;
            hasInternet = actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || 
                         actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || 
                         actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        } else {
            try {
                hasInternet = connectivityManager.getActiveNetworkInfo() != null && 
                             connectivityManager.getActiveNetworkInfo().isConnected();
            } catch (Exception e) {
                hasInternet = false;
            }
        }
        return hasInternet;
    }

    public static boolean devOptions(Context context) {
        boolean hasRestriction = false;
        UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
        try {
            int settingEnabled = Settings.Global.getInt(
                context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                Build.TYPE.equals("eng") ? 1 : 0
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hasRestriction = um.hasUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES);
            }
            return false;
        } catch (Exception e) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    hasRestriction = um.hasUserRestriction(UserManager.DISALLOW_DEBUGGING_FEATURES);
                }
            } catch (Exception ex) {}
        }
        return hasRestriction;
    }

    public static void clearUserData(Context context) {
        if (context == null) return;
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .clear()
            .commit();
        setTokenId(context);
    }

    public static void setTokenId(Context context) {
        if (context == null) return;
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString("TOKENIS", TOKENIS)
            .commit();
        Log.i("token", " pref time : " + TOKENIS);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                AppCompatActivity.INPUT_METHOD_SERVICE
            );
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printToast(String msg, View view) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public static final String[] PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? 
        new String[] {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS
        } : 
        new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
        };

    public static void dialNo(String numbers) {
        try {
            Uri u = Uri.parse("tel:" + numbers);
            Intent i = new Intent(Intent.ACTION_DIAL, u);
            MyApplication.applicationContext().startActivity(i);
        } catch (SecurityException s) {}
    }
}