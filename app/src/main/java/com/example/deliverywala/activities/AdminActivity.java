package com.example.deliverywala.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.deliverywala.R;
import com.example.deliverywala.fragments.AdminOrderFragment;
import com.example.deliverywala.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean isMainFragment = true;
    private Button btnAddItem;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
//        toolbar.navigationIcon=resources.getDrawable(R.drawable.ic_home)
        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnLogout = findViewById(R.id.btnLogout);
        
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AdminActivity.this);
                dialog.setIcon(R.drawable.ic_log_out);
                dialog.setMessage("Are you sure , you want to log out?");
                dialog.setPositiveButton("Yes", (text, listener) -> {
                    sharedPreferences.edit().clear().apply();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
                dialog.setNegativeButton("No", (text, listener) -> {});
                dialog.create();
                dialog.show();
            }
        });

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminAddFoodItem.class);
                startActivity(intent);
            }
        });

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference("user")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Object name = snapshot.child("name").getValue();
                    Object mobileNumber = snapshot.child("mobileNumber").getValue();

                //    Constants.Emp_contact = mobileNumber.toString();
                    Object email = snapshot.child("email").getValue();
                    if (sharedPreferences.getBoolean("justLoggedIn", false)) {
                        sharedPreferences.edit().putBoolean("justLoggedIn", false).apply();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Firebase", "Error reading data: " + error.getMessage());
                }
            });

        updateFirebaseToken();
        setupToolbar();
        openHome();
    }

    private void openHome() {
        isMainFragment = true;
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.frameLayout, new AdminOrderFragment())
            .commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Order details");
        }
//        Toast.makeText(this@HomeActivity, "Home", Toast.LENGTH_SHORT).show()
    }

    private void setupToolbar() {
//        setSupportActionBar(toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isMainFragment) {
            finish();
        } else {
//            supportFragmentManager.popBackStack()
            isMainFragment = true;
            openHome();
        }
    }

    /*
        @Deprecated("Deprecated in Java")
        override fun onBackPressed() {
            val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
            when (frag) {
                !is DashboardFragment -> openHome()
            }
        }
    */

    private void updateFirebaseToken() {
//        val dbRef = FirebaseDatabase.getInstance().getReference()
        FirebaseDatabase.getInstance().getReference()
            .child("user")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("firebaseToken")
            .setValue(Constants.TOKENIS); //pending= 0 , dispatch=1, delivered=2
    }
}