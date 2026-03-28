package com.example.deliverywala.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.deliverywala.R;
import com.example.deliverywala.model.User;
import com.example.deliverywala.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_NAME = "My Channel";

    private EditText etName;
    private EditText etEmail;
    private EditText etMobileNumber;
    private EditText etAddress;
    private Button btnRegister;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupToolbar();
        setupRegisterButton();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        auth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        toolbar.setTitle("Register");
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.app_color));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.ic_logo_b);


            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            String pass = etPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();
            boolean capital = false;
            boolean small = false;
            boolean specialChar = false;
            boolean number = false;

            if (etName.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
            } else if (etName.getText().length() < 4) {
                Toast.makeText(RegisterActivity.this, 
                    "Name should contain atleast 3 characters", Toast.LENGTH_LONG).show();
            } else if (etEmail.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Email Id", Toast.LENGTH_LONG).show();
            } else if (etMobileNumber.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this, 
                    "Enter Mobile Number", Toast.LENGTH_LONG).show();
            } else if (etMobileNumber.getText().length() != 10) {
                Toast.makeText(RegisterActivity.this,
                    "Mobile Number should have 10 digits", Toast.LENGTH_LONG).show();
            } else if (etAddress.getText().toString().isEmpty()) {
                Toast.makeText(RegisterActivity.this,
                    "Enter Delivery Address", Toast.LENGTH_LONG).show();
            } else if (pass.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(RegisterActivity.this,
                    "Passwords doesn't match. Please enter Same Passwords", Toast.LENGTH_LONG).show();
            } else if (pass.length() < 8) {
                Toast.makeText(RegisterActivity.this,
                    "Password size should be at least 8", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i < pass.length(); i++) {
                    char c = pass.charAt(i);
                    if (Character.isUpperCase(c)) {
                        capital = true;
                    }
                    if (Character.isLowerCase(c)) {
                        small = true;
                    }
                    if (Character.isDigit(c)) {
                        number = true;
                    }
                    if (c >= '!' && c <= '~' && !Character.isLetterOrDigit(c)) {
                        specialChar = true;
                    }
                }

                if (!small) {
                    Toast.makeText(RegisterActivity.this,
                        "Password must contain a small letter (a,b,..z)", Toast.LENGTH_SHORT).show();
                } else if (!capital) {
                    Toast.makeText(RegisterActivity.this,
                        "Password must contain a Capital letter (A,B..Z", Toast.LENGTH_SHORT).show();
                } else if (!number) {
                    Toast.makeText(RegisterActivity.this,
                        "Password must contain a numeric digit (0,1,..9)", Toast.LENGTH_SHORT).show();
                } else if (!specialChar) {
                    Toast.makeText(RegisterActivity.this,
                        "Password must contain a special Character (!,@,#..)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                    signup(
                        etName.getText().toString(),
                        etEmail.getText().toString(),
                        etMobileNumber.getText().toString(),
                        etAddress.getText().toString(),
                        pass
                    );
                }
            }
        });
    }

    private void signup(String name, String email, String mobileNumber, String address, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    dbRef = FirebaseDatabase.getInstance().getReference();
                    dbRef.child("user").child(auth.getCurrentUser().getUid())
                        .setValue(new User(
                            auth.getCurrentUser().getUid(),
                            name,
                            email,
                            mobileNumber,
                            address,
                            Constants.TOKENIS
                        ));
                    showNotification(
                        this,
                        "Welcome " + name + " to the " + getResources().getString(R.string.app_name),
                        "Explore and order your Items now!!😋"
                    );
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                }
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
            .setSmallIcon(R.drawable.ic_logo_b_round)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}