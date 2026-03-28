package com.example.deliverywala;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.multidex.MultiDex;

import com.example.deliverywala.util.Constants;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class MyApplication extends Application {
    private static final AtomicReference<File> cacheDirectory = new AtomicReference<>();
    private static MyApplication instance;
    
    {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cacheDirectory.set(getCacheDir());
        
        Context context = MyApplication.applicationContext();
        
        try {
            FirebaseApp.initializeApp(this);
            getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getToken() {
        try {
            if (Constants.TOKENIS.equals("")) {
                try {
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            try {
                                String token = task.getResult();
                                Constants.TOKENIS = token;
                                Log.e("getToken", Constants.TOKENIS);
                                FirebaseMessaging.getInstance().subscribeToTopic("all");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Constants.TOKENIS = "TokenNotGenerated";
                            Log.e("getToken", Constants.TOKENIS);
                        }
                    });
                } catch (Exception e) {
                    Log.e("getToken", e.toString());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public static synchronized MyApplication getInstance() {
        return instance;
    }

    public static Context applicationContext() {
        return instance.getApplicationContext();
    }

    public static void clearApplicationData() {
        File applicationDirectory = new File(cacheDirectory.get().getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    if (!fileName.equals("lib")) {
                        deleteFile(new File(applicationDirectory, fileName));
                    }
                }
            }
        }
    }

    private static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                if (children != null) {
                    for (String child : children) {
                        deletedAll = deleteFile(new File(file, child)) && deletedAll;
                    }
                }
            } else {
                deletedAll = file.delete();
            }
        }
        return deletedAll;
    }
}