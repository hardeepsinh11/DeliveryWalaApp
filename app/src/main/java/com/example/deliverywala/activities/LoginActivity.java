package com.example.deliverywala.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.deliverywala.R;
import com.example.deliverywala.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private TextView txtForgotPassword;
    private TextView txtSignUp;
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    private Toolbar toolbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupToolbar();
        checkLoggedInStatus();
        setupClickListeners();
    }

    private void initializeViews() {
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE);
        toolbar = findViewById(R.id.toolbar);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtSignUp = findViewById(R.id.txtSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        
        // Set default credentials for testing
        //admin
        etEmail.setText("admin@admin.com");
        etPassword.setText("Pass@123");

        // Set default credentials for testing
        //user
//        etEmail.setText("user@user.com");
//        etPassword.setText("Pass@123#");
    }


    private void setupToolbar() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.app_color));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.ic_logo_b);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Login");
        }
    }

    private void checkLoggedInStatus() {
        FirebaseUser currentUser = auth.getCurrentUser();
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (currentUser != null && isLoggedIn) {
            String savedEmail = sharedPreferences.getString("email", "");
            Intent intent;
            if (savedEmail.equals(Constants.admin)) {
                intent = new Intent(LoginActivity.this, AdminActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, HomeActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }


    private void setupClickListeners() {
        txtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter email address", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    sharedPreferences.edit()
                        .putBoolean("isLoggedIn", true)
                        .putBoolean("justLoggedIn", true)
                        .apply();
                    
                    Intent intent;
                    if (email.equals(Constants.admin)) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                }
            });
    }
}