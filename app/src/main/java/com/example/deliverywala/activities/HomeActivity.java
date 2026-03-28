package com.example.deliverywala.activities;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.deliverywala.R;
import com.example.deliverywala.fragments.DashboardFragment;
import com.example.deliverywala.fragments.OrderHistoryFragment;
import com.example.deliverywala.fragments.ProfileFragment;
import com.example.deliverywala.fragments.SettingsFragment;
import com.example.deliverywala.util.Constants;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_NAME = "My Channel";

    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayout;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private NavigationView navigationView;
    private TextView txtUserName;
    private TextView txtContactDetails;
    private MenuItem previousmenuItem;
    private boolean isMainFragment = true;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        toolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.frameLayout);
        navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        txtUserName = headerView.findViewById(R.id.txtUserName);
        txtContactDetails = headerView.findViewById(R.id.txtContactDetails);

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE);
        updateFirebaseToken();
        checkPermissions();




        FirebaseDatabase.getInstance().getReference("user")
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);
                        Constants.Emp_contact = mobileNumber;
                        String email = snapshot.child("email").getValue(String.class);
                        Constants.Emp_contact = mobileNumber;
                        txtUserName.setText(name);
                        txtContactDetails.setText(mobileNumber);

                        if (sharedPreferences.getBoolean("justLoggedIn", false)) {
                            sharedPreferences.edit().putBoolean("justLoggedIn", false).apply();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error reading data: " + error.getMessage());
                    }
                });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ProfileFragment())
                        .commit();
                getSupportActionBar().setTitle("My Profile");
                drawerLayout.closeDrawers();
                navigationView.setCheckedItem(R.id.myProfile);
            }
        });

        setupToolbar();
        openHome();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (previousmenuItem != null) {
                    previousmenuItem.setChecked(false);
                }
                item.setCheckable(true);
                item.setChecked(true);
                previousmenuItem = item;

                int id = item.getItemId();
                if (id == R.id.home) {
                    isMainFragment = true;
                    openHome();
                } else if (id == R.id.myProfile) {
                    isMainFragment = false;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new ProfileFragment())
                            .commit();
                    drawerLayout.closeDrawers();
                    getSupportActionBar().setTitle("My profile");
                } else if (id == R.id.orderHistory) {
                    isMainFragment = false;
                    sharedPreferences.edit().putBoolean("newToOld", true).apply();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new OrderHistoryFragment())
                            .commit();
                    drawerLayout.closeDrawers();
                    getSupportActionBar().setTitle("Order History");
                } else if (id == R.id.settings) {
                    isMainFragment = false;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, new SettingsFragment())
                            .commit();
                    drawerLayout.closeDrawers();
                    getSupportActionBar().setTitle("Settings");
                } else if (id == R.id.logOut) {
                    isMainFragment = true;
                    drawerLayout.closeDrawers();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                    dialog.setTitle("Log Out?");
                    dialog.setIcon(R.drawable.ic_log_out);
                    dialog.setMessage("Are you sure , you want to log out?");
                    dialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                        sharedPreferences.edit().clear().apply();
                        auth.signOut();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                    dialog.setNegativeButton("No", (dialogInterface, i) -> {});
                    dialog.create();
                    dialog.show();
                }
                return true;
            }
        });
    }
    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(isGranted -> {
                Log.e("isGranted", "Doneee");
            });
        } else {
            requestPermission(isGranted -> {
                Log.e("isGranted", "Doneee");
            });
        }
    }

    // Add this method to your class
    private void requestPermission(AdminAddFoodItem.PermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Constants.PERMISSIONS[0])
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        Constants.PERMISSIONS,
                        PERMISSION_REQUEST_CODE
                );
            } else {
                callback.onPermissionResult(true);
            }
        } else {
            callback.onPermissionResult(true);
        }
    }

    // Add this interface definition


    // Add this constant
    private static final int PERMISSION_REQUEST_CODE = 1001;

    // Also add this method to handle permission results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission", "Granted");
            } else {
                Log.e("Permission", "Denied");
            }
        }
    }


    private void openHome() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new DashboardFragment())
                .commit();
        drawerLayout.closeDrawers();
        getSupportActionBar().setTitle("Home");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_b)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isMainFragment) {
            finish();
        } else {
            isMainFragment = true;
            openHome();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateFirebaseToken() {
        FirebaseDatabase.getInstance().getReference()
                .child("user")
                .child(auth.getCurrentUser().getUid())
                .child("firebaseToken")
                .setValue(Constants.TOKENIS);
    }
}