package com.example.deliverywala.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.deliverywala.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditAccountActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etMobileNumber;
    private EditText etAddress;
    private Button btnUpdate;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        initializeViews();
        setupToolbar();
        loadUserData();
        setupUpdateButton();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etAddress = findViewById(R.id.etAddress);
        btnUpdate = findViewById(R.id.btnUpdate);
        toolbar = findViewById(R.id.toolbar);
        auth = FirebaseAuth.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Details");
        }
    }

    private void loadUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user")
                .child(auth.getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);
                
                etName.setHint(name);
                etAddress.setHint(address);
                etMobileNumber.setHint(mobileNumber);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error reading data: " + error.getMessage());
            }
        });
    }

    private void setupUpdateButton() {
        btnUpdate.setOnClickListener(v -> {
            if (etName.getText().toString().isEmpty()) {
                Toast.makeText(EditAccountActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
            } 
            else if (etName.getText().length() < 4) {
                Toast.makeText(EditAccountActivity.this, 
                    "Name should contain atleast 3 characters", Toast.LENGTH_LONG).show();
            } 
            else if (etMobileNumber.getText().toString().isEmpty()) {
                Toast.makeText(EditAccountActivity.this, 
                    "Enter Mobile Number", Toast.LENGTH_LONG).show();
            } 
            else if (etMobileNumber.getText().length() != 10) {
                Toast.makeText(EditAccountActivity.this,
                    "Mobile Number should have 10 digits", Toast.LENGTH_LONG).show();
            } 
            else if (etAddress.getText().toString().isEmpty()) {
                Toast.makeText(EditAccountActivity.this,
                    "Enter Delivery Address", Toast.LENGTH_LONG).show();
            } 
            else {
                updateUserDetails();
            }
        });
    }

    private void updateUserDetails() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("user").child(auth.getCurrentUser().getUid()).child("name")
            .setValue(etName.getText().toString());
        dbRef.child("user").child(auth.getCurrentUser().getUid()).child("mobileNumber")
            .setValue(etMobileNumber.getText().toString());
        dbRef.child("user").child(auth.getCurrentUser().getUid()).child("address")
            .setValue(etAddress.getText().toString());
            
        Toast.makeText(EditAccountActivity.this,
            "Details Updated Successfully", Toast.LENGTH_SHORT).show();
            
        Intent intent = new Intent(EditAccountActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}