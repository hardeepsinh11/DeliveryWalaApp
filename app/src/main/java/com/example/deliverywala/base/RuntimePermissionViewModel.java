package com.example.deliverywala.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;

import com.example.deliverywala.R;

public abstract class RuntimePermissionViewModel extends AndroidViewModel implements ReliableViewModel {
    private Activity activity;
    private PermissionCallback callback;

    public interface PermissionCallback {
        void onResult(boolean isGranted);
    }

    public RuntimePermissionViewModel(Application application) {
        super(application);
    }

    public void requestPermission(Activity activity, String[] permissions, PermissionCallback callback) {
        this.activity = activity;
        this.callback = callback;
        


                if (checkPermissions(activity, permissions)) {
                    callback.onResult(true);
                } else {
                    activity.requestPermissions(permissions, Integer.MAX_VALUE);
                }

    }

    public boolean checkPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Integer.MAX_VALUE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                if (callback != null) {
                    callback.onResult(true);
                }
            } else {
                onDenied();
            }
        }
    }

    private void onDenied() {
        if (callback != null) {
            callback.onResult(false);
        }
        if (activity != null) {
            setAlertMessage(activity);
        }
    }

    private void setAlertMessage(Activity activity) {
        AlertDialog.Builder adb = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        
        adb.setTitle(activity.getString(R.string.app_name));
        String msg = "<p>Dear User, </p>" +
                "<p>Seems like you have <b>\"Denied\"</b> the minimum requirement permission to access more features of application.</p>" +
                "<p>You must have to <b>\"Allow\"</b> all permission. We will not share your data with anyone else.</p>" +
                "<p>Do you want to enable all requirement permission ?</p>" +
                "<p>Go To : Settings >> App > " + activity.getString(R.string.app_name) + " Permission : Allow ALL</p>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            adb.setMessage(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));
        } else {
            adb.setMessage(Html.fromHtml(msg));
        }

        adb.setPositiveButton("Allow All", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);

        });

        adb.setNegativeButton("Remind Me Later", (dialog, which) -> dialog.dismiss());

        adb.show();

    }

    @Override
    public void writeTo(Bundle bundle) {
        // Default implementation
    }

    @Override
    public void readFrom(Bundle bundle) {
        // Default implementation
    }
}